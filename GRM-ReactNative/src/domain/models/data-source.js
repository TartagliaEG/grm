import { map } from 'rxjs/operators'

export class DataSource {
  constructor(source, data) {
    this._source = source;
    this._data = data;
  }

  data = () => this._data;
  source = () => this._source;

  isMemory = () => this._source === 'MEMORY';
  isNetwork = () => this._source === 'NETWORK';
  isDatabase = () => this._source === 'DATABASE';
  isEmpty = () => this._source === 'NONE';
}

export const memory = (data) => new DataSource('MEMORY', data);
export const database = (data) => new DataSource('DATABASE', data);
export const network = (data) => new DataSource('NETWORK', data);
export const empty = (data) => new DataSource('NONE', data);

export const rxMemory = (rxData) => rxData.pipe(map(data => memory(data)));
export const rxDatabase = (rxData) => rxData.pipe(map(data => database(data)));
export const rxNetwork = (rxData) => rxData.pipe(map(data => network(data)));
export const rxEmpty = (rxData) => rxData.pipe(map(data => none(data)));
