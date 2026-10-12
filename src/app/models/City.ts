import { BaseModel } from "./base-model";

export interface City extends BaseModel {
    id: number;
    name: string;
    code: string;
}
