export interface Category {
  id: number
  name: string
  budgetAmount: number
}

export interface ExpenseItem {
  id: number
  title: string
  price: number
  expenseDate: string
  categoryId: number | null
  categoryName: string | null
  memo: string | null
}

export interface ExpenseListResponse {
  expenses: ExpenseItem[]
  currentPage: number
  perPage: number
  totalCount: number
  totalPages: number
}
