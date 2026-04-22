export interface SalaryRequest {
    grossSalary: number;
    taxYear: string;
    studentLoanPlan: string;
    payPeriod: string;
    hoursPerWeek?: number;
    workingDaysPerWeek?: number;
    deductions?: DeductionRequest[];
}

export interface DeductionRequest {
    label: string;
    type: string;
    mode: string;
    value: number;
}

export interface SalaryResponse {
    grossAnnual: string;
    taxFreeAllowance: string;
    taxableIncome: string;
    incomeTax: string;
    nationalInsurance: string;
    studentLoanRepayment: string;
    pensionContribution: string;
    salarySacrifice: string;
    giftAid: string;
    professionalSubscription: string;
    other: string;
    totalPreTaxDeductions: string;
    totalDeductions: string;
    netAnnual: string;
    effectiveTaxRate: string;
    grossPerPeriod: string;
    netPerPeriod: string;
    payPeriod: string;
}
