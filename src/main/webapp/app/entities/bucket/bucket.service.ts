import { Injectable } from '@angular/core';
import { Http, Response } from '@angular/http';
import { Observable } from 'rxjs/Rx';
import { DateUtils } from 'ng-jhipster';

import { Bucket } from './bucket.model';
import { ResponseWrapper, createRequestOption } from '../../shared';

@Injectable()
export class BucketService {

    private resourceUrl = 'api/buckets';
    private resourceSearchUrl = 'api/_search/buckets';

    constructor(private http: Http, private dateUtils: DateUtils) { }

    create(bucket: Bucket): Observable<Bucket> {
        const copy = this.convert(bucket);
        return this.http.post(this.resourceUrl, copy).map((res: Response) => {
            const jsonResponse = res.json();
            this.convertItemFromServer(jsonResponse);
            return jsonResponse;
        });
    }

    update(bucket: Bucket): Observable<Bucket> {
        const copy = this.convert(bucket);
        return this.http.put(this.resourceUrl, copy).map((res: Response) => {
            const jsonResponse = res.json();
            this.convertItemFromServer(jsonResponse);
            return jsonResponse;
        });
    }

    find(id: number): Observable<Bucket> {
        return this.http.get(`${this.resourceUrl}/${id}`).map((res: Response) => {
            const jsonResponse = res.json();
            this.convertItemFromServer(jsonResponse);
            return jsonResponse;
        });
    }

    query(req?: any): Observable<ResponseWrapper> {
        const options = createRequestOption(req);
        return this.http.get(this.resourceUrl, options)
            .map((res: Response) => this.convertResponse(res));
    }

    delete(id: number): Observable<Response> {
        return this.http.delete(`${this.resourceUrl}/${id}`);
    }

    search(req?: any): Observable<ResponseWrapper> {
        const options = createRequestOption(req);
        return this.http.get(this.resourceSearchUrl, options)
            .map((res: any) => this.convertResponse(res));
    }

    private convertResponse(res: Response): ResponseWrapper {
        const jsonResponse = res.json();
        for (let i = 0; i < jsonResponse.length; i++) {
            this.convertItemFromServer(jsonResponse[i]);
        }
<<<<<<< HEAD
        return new ResponseWrapper(res.headers, jsonResponse, res.status);
=======
        return new ResponseWrapper(res.headers, jsonResponse);
>>>>>>> with_entities
    }

    private convertItemFromServer(entity: any) {
        entity.data = this.dateUtils
            .convertDateTimeFromServer(entity.data);
    }

    private convert(bucket: Bucket): Bucket {
        const copy: Bucket = Object.assign({}, bucket);

        copy.data = this.dateUtils.toDate(bucket.data);
        return copy;
    }
}
