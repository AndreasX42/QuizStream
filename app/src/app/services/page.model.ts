export interface Page<T> {
  content: T[];
  page: PageInfo;
}

export interface PageInfo {
  totalPages: number;
  totalElements: number;
  size: number;
  number: number; // Current page number
}
