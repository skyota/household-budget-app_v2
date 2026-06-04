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

export interface ListResponse<T> {
  data: T[]
  currentPage: number
  perPage: number
  totalCount: number
  totalPages: number
}

export interface FixedExpense {
  id: number
  title: string
  price: number
  fixedExpenseDate: number
  memo: string | null
}

export interface IncomeSetting {
  id: number
  title: string
  amount: number
  incomeDate: number
  memo: string | null
  isAutoGenerate: boolean
}

export interface InvestSetting {
  amount: number
  investDate: number
}

export type InvestType = 'INITIAL' | 'REGULAR' | 'SPOT'

export interface InvestRecord {
  id: number
  amount: number
  investDate: string
  investType: InvestType
  memo: string | null
}

export type BalanceType = 'INITIAL' | 'ADJUSTMENT'

export interface CashBalance {
  id: number
  amount: number
  balanceDate: string
  balanceType: BalanceType
  memo: string | null
}

export interface AssetSummary {
  cashBalance: number
  nisaPrincipal: number
  totalAssets: number
}
