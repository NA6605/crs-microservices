export interface Course {
    id: number;
    tenMonHoc: string;
    soTinChi: number;
    soChoToiDa: number;
    soChoConLai: number;
}
export interface PagedResponse<T> {
    content: T[];
    totalElements: number;
    totalPages: number;
    number: number; // trang hien tai (bat dau tu 0)
    size: number;
}
export interface CourseFormValues {
    tenMonHoc: string;
    soTinChi: string; // Dùng string trong form để dễ kiểm soát input rỗng
    soChoToiDa: string;
}

export const emptyCourseForm: CourseFormValues = {
    tenMonHoc: '',
    soTinChi: '',
    soChoToiDa: '',
};