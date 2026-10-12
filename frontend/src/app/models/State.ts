import { BaseModel } from "./base-model";

export interface State extends BaseModel {
    id: number;
    name: string;
    code: string;
    country: any;
    city: any[];
}
