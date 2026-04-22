import { SalaryRequest,SalaryResponse } from "../types";

const API_BASE_URL = process.env.NEXT_PUBLIC_API_BASE_URL || "http://localhost:8080";

export async function calculateSalary(salaryRequest: SalaryRequest): Promise<SalaryResponse> {
    const response = await fetch(`${API_BASE_URL}/api/salary/calculate`, {
        method: "POST",
        headers:{
            "Content-Type": "application/json"
        },
        body: JSON.stringify(salaryRequest)
    });

    if (!response.ok) {
        throw new Error(`Error: ${response.statusText}`);
    }

    return response.json();
}

export async function getCurrentTaxYear(): Promise<string> {
  try {
    const response = await fetch(`${API_BASE_URL}/api/salary/tax-years/current`);
    if (!response.ok) return '2026/27';
    const data = await response.json();
    return data.label;
  } catch {
    return '2026/27';
  }
}