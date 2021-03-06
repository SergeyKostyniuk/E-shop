package com.social.eshop.web.rest;

import com.social.eshop.EshopApp;
import com.social.eshop.domain.Seen;
import com.social.eshop.repository.SeenRepository;
import com.social.eshop.repository.search.SeenSearchRepository;
import com.social.eshop.service.SeenService;
import com.social.eshop.service.dto.SeenDTO;
import com.social.eshop.service.mapper.SeenMapper;
import com.social.eshop.web.rest.errors.ExceptionTranslator;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = EshopApp.class)
public class SeenResourceTest {

    private static final LocalDate DEFAULT_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_DATE = LocalDate.now(ZoneId.systemDefault());

    @Autowired
    private SeenRepository seenRepository;

    @Autowired
    private SeenMapper seenMapper;

    @Autowired
    private SeenService seenService;

    @Autowired
    private SeenSearchRepository seenSearchRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restSeenMockMvc;

    private Seen seen;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        SeenResource seenResource = new SeenResource(seenService);
        this.restSeenMockMvc = MockMvcBuilders.standaloneSetup(seenResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    public static Seen createEntity(EntityManager em) {
        Seen seen = new Seen()
            .date(DEFAULT_DATE);
        return seen;
    }

    @Before
    public void initTest() {
        seenSearchRepository.deleteAll();
        seen = createEntity(em);
    }

    @Test
    @Transactional
    public void createSeen() throws Exception {
        int databaseSizeBeforeCreate = seenRepository.findAll().size();

        // Create the Seen
        SeenDTO seenDTO = seenMapper.toDto(seen);
        restSeenMockMvc.perform(post("/api/seens")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(seenDTO)))
            .andExpect(status().isCreated());

        // Validate the Seen in the database
        List<Seen> seenList = seenRepository.findAll();
        assertThat(seenList).hasSize(databaseSizeBeforeCreate + 1);
        Seen testSeen = seenList.get(seenList.size() - 1);
        assertThat(testSeen.getDate()).isEqualTo(DEFAULT_DATE);

        // Validate the Seen in Elasticsearch
        Seen seenEs = seenSearchRepository.findOne(testSeen.getId());
        assertThat(seenEs).isEqualToComparingFieldByField(testSeen);
    }

    @Test
    @Transactional
    public void getAllSeens() throws Exception {
        // Initialize the database
        seenRepository.saveAndFlush(seen);

        // Get all the seenList
        restSeenMockMvc.perform(get("/api/seens?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(seen.getId().intValue())))
            .andExpect(jsonPath("$.[*].date").value(hasItem(DEFAULT_DATE.toString())));
    }

    @Test
    @Transactional
    public void getSeen() throws Exception {
        // Initialize the database
        seenRepository.saveAndFlush(seen);

        // Get the seen
        restSeenMockMvc.perform(get("/api/seens/{id}", seen.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(seen.getId().intValue()))
            .andExpect(jsonPath("$.date").value(DEFAULT_DATE.toString()));
    }

    @Test
    @Transactional
    public void updateSeen() throws Exception {
        // Initialize the database
        seenRepository.saveAndFlush(seen);
        seenSearchRepository.save(seen);
        int databaseSizeBeforeUpdate = seenRepository.findAll().size();

        // Update the seen
        Seen updatedSeen = seenRepository.findOne(seen.getId());
        updatedSeen
            .date(UPDATED_DATE);
        SeenDTO seenDTO = seenMapper.toDto(updatedSeen);

        restSeenMockMvc.perform(put("/api/seens")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(seenDTO)))
            .andExpect(status().isOk());

        // Validate the Seen in the database
        List<Seen> seenList = seenRepository.findAll();
        assertThat(seenList).hasSize(databaseSizeBeforeUpdate);
        Seen testSeen = seenList.get(seenList.size() - 1);
        assertThat(testSeen.getDate()).isEqualTo(UPDATED_DATE);

        // Validate the Seen in Elasticsearch
        Seen seenEs = seenSearchRepository.findOne(testSeen.getId());
        assertThat(seenEs).isEqualToComparingFieldByField(testSeen);
    }

    @Test
    @Transactional
    public void deleteSeen() throws Exception {
        // Initialize the database
        seenRepository.saveAndFlush(seen);
        seenSearchRepository.save(seen);
        int databaseSizeBeforeDelete = seenRepository.findAll().size();

        // Get the seen
        restSeenMockMvc.perform(delete("/api/seens/{id}", seen.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate Elasticsearch is empty
        boolean seenExistsInEs = seenSearchRepository.exists(seen.getId());
        assertThat(seenExistsInEs).isFalse();

        // Validate the database is empty
        List<Seen> seenList = seenRepository.findAll();
        assertThat(seenList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchSeen() throws Exception {
        // Initialize the database
        seenRepository.saveAndFlush(seen);
        seenSearchRepository.save(seen);

        // Search the seen
        restSeenMockMvc.perform(get("/api/_search/seens?query=id:" + seen.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(seen.getId().intValue())))
            .andExpect(jsonPath("$.[*].date").value(hasItem(DEFAULT_DATE.toString())));
    }
}
