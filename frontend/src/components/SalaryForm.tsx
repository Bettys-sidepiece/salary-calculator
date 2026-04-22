'use client';
import { useState, useEffect } from "react";
import { calculateSalary, getCurrentTaxYear } from "../services/api";
import { SalaryResponse } from "../types";

interface Deduction {
    label: string;
    type: string;
    mode: string;
    value: string;
}

export default function SalaryForm() {
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState('');
    const [result, setResult] = useState<SalaryResponse | null>(null);
    const [showDeductionsForm, setShowDeductionsForm] = useState(false);

    const [formData, setFormData] = useState({
        grossSalary: '',
        taxYear: '2026/27',
        studentLoanPlan: 'NONE',
        payPeriod: 'ANNUALLY',
        hoursPerWeek: '37.5',
        workingDaysPerWeek: '5',
        deductions: [] as Deduction[]
    });

    const [viewPeriod, setViewPeriod] = useState<'ANNUALLY' | 'MONTHLY' | 'BI_WEEKLY' | 'WEEKLY' | 'DAILY' | 'HOURLY'>('ANNUALLY');

    // Fetch current tax year on mount
    useEffect(() => {
        getCurrentTaxYear().then(year => {
            setFormData(prev => ({ ...prev, taxYear: year }));
        });
    }, []);

    const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) => {
        const { name, value } = e.target;
        setFormData(prev => ({
            ...prev,
            [name]: name === 'grossSalary' ? value : value
        }));
    };

    // Handle deduction changes
    const handleDeductionChange = (index: number, field: string, value: string) => {
        const newDeductions = [...formData.deductions];
        newDeductions[index] = { ...newDeductions[index], [field]: value };
        setFormData(prev => ({ ...prev, deductions: newDeductions }));
    };

    // Add new deduction
    const addDeduction = () => {
        setFormData(prev => ({
            ...prev,
            deductions: [...prev.deductions, { label: '', type: 'PENSION', mode: 'FIXED', value: '' }]
        }));
    };

    // Remove deduction
    const removeDeduction = (index: number) => {
        setFormData(prev => ({
            ...prev,
            deductions: prev.deductions.filter((_, i) => i !== index)
        }));
    };

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        setLoading(true);
        setError('');
        setResult(null);

        try {
            const data = await calculateSalary({
                grossSalary: parseFloat(formData.grossSalary) || 0,
                taxYear: formData.taxYear,
                studentLoanPlan: formData.studentLoanPlan,
                payPeriod: formData.payPeriod,
                hoursPerWeek: parseFloat(formData.hoursPerWeek) || 37.5,
                workingDaysPerWeek: parseInt(formData.workingDaysPerWeek) || 5,
                deductions: formData.deductions.map(d => {
                    const value = parseFloat(d.value) || 0;
                    const finalValue = d.mode === 'PERCENTAGE' ? value / 100 : value;
                    return {
                        label: d.label,
                        type: d.type,
                        mode: d.mode,
                        value: finalValue
                    };
                })
            });
            setResult(data);
        } catch (err: any) {
            setError(err instanceof Error ? err.message : 'Calculation failed');
        } finally {
            setLoading(false);
        }
    };

    const getPeriodMultiplier = (period: typeof viewPeriod): number => {
        const workingDaysPerYear = parseInt(formData.workingDaysPerWeek) * 52;
        const hoursPerYear = parseFloat(formData.hoursPerWeek) * 52;
        
        switch (period) {
            case 'ANNUALLY': return 1;
            case 'MONTHLY': return 12;
            case 'BI_WEEKLY': return 26;
            case 'WEEKLY': return 52;
            case 'DAILY': return workingDaysPerYear;
            case 'HOURLY': return hoursPerYear;
            default: return 1;
        }
    };

    const convertResult = (period: typeof viewPeriod) => {
        if (!result) return null;
        const multiplier = getPeriodMultiplier(period);
        return {
            ...result,
            grossAnnual: (parseFloat(result.grossAnnual) / multiplier).toFixed(2),
            taxFreeAllowance: (parseFloat(result.taxFreeAllowance) / multiplier).toFixed(2),
            taxableIncome: (parseFloat(result.taxableIncome) / multiplier).toFixed(2),
            incomeTax: (parseFloat(result.incomeTax) / multiplier).toFixed(2),
            nationalInsurance: (parseFloat(result.nationalInsurance) / multiplier).toFixed(2),
            studentLoanRepayment: (parseFloat(result.studentLoanRepayment) / multiplier).toFixed(2),
            pensionContribution: (parseFloat(result.pensionContribution) / multiplier).toFixed(2),
            salarySacrifice: (parseFloat(result.salarySacrifice) / multiplier).toFixed(2),
            giftAid: (parseFloat(result.giftAid) / multiplier).toFixed(2),
            professionalSubscription: (parseFloat(result.professionalSubscription) / multiplier).toFixed(2),
            other: (parseFloat(result.other) / multiplier).toFixed(2),
            totalPreTaxDeductions: (parseFloat(result.totalPreTaxDeductions) / multiplier).toFixed(2),
            totalDeductions: (parseFloat(result.totalDeductions) / multiplier).toFixed(2),
            netAnnual: (parseFloat(result.netAnnual) / multiplier).toFixed(2),
            netPerPeriod: (parseFloat(result.netPerPeriod) / multiplier).toFixed(2),
            payPeriod: period.replace('_', ' ')
        };
    };

    return (
        <div className="w-full max-w-6xl mx-auto">
            <div className="bg-white rounded-lg shadow-lg p-8">
                <form onSubmit={handleSubmit} className="space-y-6">
                    {/* Gross Salary */}
                    <div>
                        <label className="block text-sm font-medium text-black mb-2">
                            Annual Gross Salary (£)
                        </label>
                        <input
                            type="number"
                            name="grossSalary"
                            value={formData.grossSalary}
                            onChange={handleChange}
                            className="w-full text-black px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                            placeholder="e.g. 50000"
                            min="0"
                            required
                        />
                    </div>

                    {/* Student Loan Plan */}
                    <div>
                        <label className="block text-sm font-medium text-black mb-2">
                            Student Loan Plan
                        </label>
                        <select
                            name="studentLoanPlan"
                            value={formData.studentLoanPlan}
                            onChange={handleChange}
                            className="w-full text-black px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                        >
                            <option value="NONE">None</option>
                            <option value="PLAN1">Plan 1 (threshold: £22,015)</option>
                            <option value="PLAN2">Plan 2 (threshold: £27,715)</option>
                            <option value="PLAN_4">Plan 4 (threshold: £27,665)</option>
                            <option value="POSTGRAD">Postgraduate (threshold: £21,000)</option>
                        </select>
                    </div>

                    {/* Pay Period */}
                    <div>
                        <label className="block text-sm font-medium text-black mb-2">
                            Pay Period
                        </label>
                        <select
                            name="payPeriod"
                            value={formData.payPeriod}
                            onChange={handleChange}
                            className="w-full text-black px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                        >
                            <option value="ANNUALLY">Annually</option>
                            <option value="MONTHLY">Monthly</option>
                            <option value="WEEKLY">Weekly</option>
                            <option value="DAILY">Daily</option>
                            <option value="HOURLY">Hourly</option>
                        </select>
                    </div>

                    {/* Deductions Section */}
                    <div className="border-t pt-6">
                        <div className="flex items-center justify-between mb-4">
                            <h3 className="text-lg font-semibold text-black">Pre-Tax Deductions</h3>
                            <button
                                type="button"
                                onClick={() => setShowDeductionsForm(!showDeductionsForm)}
                                className="text-sm px-3 py-1 bg-gray-200 hover:bg-gray-300 rounded transition text-black"
                            >
                                {showDeductionsForm ? 'Hide' : 'Add Deduction'}
                            </button>
                        </div>

                        {showDeductionsForm && (
                            <div className="bg-gray-50 p-4 rounded-lg mb-4 space-y-4">
                                <div className="grid grid-cols-2 gap-3">
                                    <div>
                                        <label className="text-xs font-medium text-black">Label</label>
                                        <input
                                            type="text"
                                            placeholder="e.g. Pension"
                                            className="w-full px-2 py-1 text-sm border border-gray-300 rounded text-black"
                                            disabled
                                        />
                                    </div>
                                    <div>
                                        <label className="text-xs font-medium text-black">Type</label>
                                        <select className="w-full px-2 py-1 text-sm border border-gray-300 rounded text-black" disabled>
                                            <option>PENSION</option>
                                            <option>SALARY_SACRIFICE</option>
                                            <option>GIFT_AID</option>
                                            <option>PROFESSIONAL_SUBSCRIPTION</option>
                                            <option>OTHER</option>
                                        </select>
                                    </div>
                                    <div>
                                        <label className="text-xs font-medium text-black">Mode</label>
                                        <select className="w-full px-2 py-1 text-sm border border-gray-300 rounded text-black" disabled>
                                            <option>FIXED</option>
                                            <option>PERCENTAGE</option>
                                        </select>
                                    </div>
                                    <div>
                                        <label className="text-xs font-medium text-black">Value</label>
                                        <input type="number" placeholder="Amount or %" className="w-full px-2 py-1 text-sm border border-gray-300 rounded text-black" disabled />
                                    </div>
                                </div>
                                <button
                                    type="button"
                                    onClick={addDeduction}
                                    className="w-full text-sm px-3 py-2 bg-blue-500 text-white rounded hover:bg-blue-600 transition"
                                >
                                    + Add Deduction
                                </button>
                            </div>
                        )}

                        {/* Display Added Deductions */}
                        {formData.deductions.length > 0 && (
                            <div className="space-y-3">
                                {formData.deductions.map((deduction, index) => (
                                    <div key={index} className="bg-gray-50 p-3 rounded-lg grid grid-cols-5 gap-2 items-end">
                                        <div>
                                            <label className="text-xs font-medium text-black">Label</label>
                                            <input
                                                type="text"
                                                value={deduction.label}
                                                onChange={(e) => handleDeductionChange(index, 'label', e.target.value)}
                                                placeholder="e.g. Pension"
                                                className="w-full px-2 py-1 text-sm border border-gray-300 rounded text-black"
                                            />
                                        </div>
                                        <div>
                                            <label className="text-xs font-medium text-black">Type</label>
                                            <select
                                                value={deduction.type}
                                                onChange={(e) => handleDeductionChange(index, 'type', e.target.value)}
                                                className="w-full px-2 py-1 text-sm border border-gray-300 rounded text-black"
                                            >
                                                <option value="PENSION">Pension</option>
                                                <option value="SALARY_SACRIFICE">Salary Sacrifice</option>
                                                <option value="GIFT_AID">Gift Aid</option>
                                                <option value="PROFESSIONAL_SUBSCRIPTION">Professional Sub</option>
                                                <option value="OTHER">Other</option>
                                            </select>
                                        </div>
                                        <div>
                                            <label className="text-xs font-medium text-black">Mode</label>
                                            <select
                                                value={deduction.mode}
                                                onChange={(e) => handleDeductionChange(index, 'mode', e.target.value)}
                                                className="w-full px-2 py-1 text-sm border border-gray-300 rounded text-black"
                                            >
                                                <option value="FIXED">Fixed (£)</option>
                                                <option value="PERCENTAGE">Percentage (%)</option>
                                            </select>
                                        </div>
                                        <div>
                                            <label className="text-xs font-medium text-black">Value</label>
                                            <input
                                                type="number"
                                                value={deduction.value}
                                                onChange={(e) => handleDeductionChange(index, 'value', e.target.value)}
                                                placeholder="0"
                                                className="w-full px-2 py-1 text-sm border border-gray-300 rounded text-black"
                                            />
                                        </div>
                                        <button
                                            type="button"
                                            onClick={() => removeDeduction(index)}
                                            className="px-2 py-1 bg-red-500 text-white text-sm rounded hover:bg-red-600 transition"
                                        >
                                            Remove
                                        </button>
                                    </div>
                                ))}
                            </div>
                        )}
                    </div>

                    {/* Submit Button */}
                    <button
                        type="submit"
                        disabled={loading}
                        className="w-full bg-blue-600 text-white font-semibold py-3 rounded-lg hover:bg-blue-700 disabled:opacity-50 disabled:cursor-not-allowed transition"
                    >
                        {loading ? 'Calculating...' : 'Calculate Salary'}
                    </button>
                </form>

                {/* Error Message */}
                {error && (
                    <div className="mt-6 p-4 bg-red-100 border border-red-400 text-red-700 rounded-lg">
                        {error}
                    </div>
                )}

                {/* Results */}
                {result && (
                    <div className="mt-8 p-6 bg-gradient-to-br from-blue-50 to-indigo-50 rounded-lg">
                        <h2 className="text-2xl font-bold text-black mb-4">Tax Breakdown</h2>
                        
                        {/* View Period Selector */}
                        <div className="mb-6 pb-4 border-b border-gray-300">
                            <p className="text-sm font-semibold text-black mb-2">View by:</p>
                            <div className="flex flex-wrap gap-2">
                                {['ANNUALLY', 'MONTHLY', 'BI_WEEKLY', 'WEEKLY', 'DAILY', 'HOURLY'].map((period) => (
                                    <button
                                        key={period}
                                        onClick={() => setViewPeriod(period as typeof viewPeriod)}
                                        className={`px-3 py-1 text-sm rounded transition ${
                                            viewPeriod === period
                                                ? 'bg-blue-600 text-white'
                                                : 'bg-gray-200 text-black hover:bg-gray-300'
                                        }`}
                                    >
                                        {period === 'BI_WEEKLY' ? '2-Weekly' : period.charAt(0) + period.slice(1).toLowerCase()}
                                    </button>
                                ))}
                            </div>
                        </div>

                        {(() => {
                            const displayResult = convertResult(viewPeriod);
                            if (!displayResult) return null;

                            return (
                                <>
                                    {/* Top Summary */}
                                    <div className="grid grid-cols-3 gap-4 mb-6 pb-6 border-b border-gray-300">
                                        <div>
                                            <p className="text-sm text-gray-600">Gross</p>
                                            <p className="text-lg font-semibold text-green-600">£{parseFloat(displayResult.grossAnnual).toFixed(2)}</p>
                                        </div>
                                        <div>
                                            <p className="text-sm text-gray-600">Tax Free Allowance</p>
                                            <p className="text-lg font-semibold text-green-600">£{parseFloat(displayResult.taxFreeAllowance).toFixed(2)}</p>
                                        </div>
                                        <div>
                                            <p className="text-sm text-gray-600">Taxable Income</p>
                                            <p className="text-lg font-semibold text-green-600">£{parseFloat(displayResult.taxableIncome).toFixed(2)}</p>
                                        </div>
                                    </div>

                                    {/* Pre-Tax Deductions Section */}
                                    {parseFloat(displayResult.totalPreTaxDeductions) > 0 && (
                                        <div className="mb-6 pb-6 border-b border-gray-300">
                                            <h3 className="font-semibold text-black mb-3">
                                                Total Pre-Tax Deductions: <span className="text-red-600">£{parseFloat(displayResult.totalPreTaxDeductions).toFixed(2)}</span>
                                            </h3>
                                            <div className="space-y-2 pl-4">
                                                {parseFloat(displayResult.pensionContribution) > 0 && (
                                                    <p className="text-sm text-black">- Pension Contribution: £{parseFloat(displayResult.pensionContribution).toFixed(2)}</p>
                                                )}
                                                {parseFloat(displayResult.salarySacrifice) > 0 && (
                                                    <p className="text-sm text-black">- Salary Sacrifice: £{parseFloat(displayResult.salarySacrifice).toFixed(2)}</p>
                                                )}
                                                {parseFloat(displayResult.giftAid) > 0 && (
                                                    <p className="text-sm text-black">- Gift Aid: £{parseFloat(displayResult.giftAid).toFixed(2)}</p>
                                                )}
                                                {parseFloat(displayResult.professionalSubscription) > 0 && (
                                                    <p className="text-sm text-black">- Professional Subscription: £{parseFloat(displayResult.professionalSubscription).toFixed(2)}</p>
                                                )}
                                                {parseFloat(displayResult.other) > 0 && (
                                                    <p className="text-sm text-black">- Other: £{parseFloat(displayResult.other).toFixed(2)}</p>
                                                )}
                                            </div>
                                        </div>
                                    )}

                                    {/* Tax & NI */}
                                    <div className="grid grid-cols-2 gap-4 mb-6 pb-6 border-b border-gray-300">
                                        <div>
                                            <p className="text-sm text-gray-600">Income Tax</p>
                                            <p className="text-lg font-semibold text-red-600">£{parseFloat(displayResult.incomeTax).toFixed(2)}</p>
                                        </div>
                                        <div>
                                            <p className="text-sm text-gray-600">National Insurance</p>
                                            <p className="text-lg font-semibold text-red-600">£{parseFloat(displayResult.nationalInsurance).toFixed(2)}</p>
                                        </div>
                                        {parseFloat(displayResult.studentLoanRepayment) > 0 && (
                                            <div>
                                                <p className="text-sm text-gray-600">Student Loan Repayment</p>
                                                <p className="text-lg font-semibold text-red-600">£{parseFloat(displayResult.studentLoanRepayment).toFixed(2)}</p>
                                            </div>
                                        )}
                                    </div>

                                    {/* Total Deductions Summary */}
                                    <div className="mb-6 pb-6 border-b border-gray-300">
                                        <h3 className="font-semibold text-black mb-3">
                                            Total Deductions: <span className="text-red-600">£{parseFloat(displayResult.totalDeductions).toFixed(2)}</span>
                                        </h3>
                                        <div className="space-y-2 pl-4">
                                            {parseFloat(displayResult.totalPreTaxDeductions) > 0 && (
                                                <p className="text-sm text-black">- Pre-Tax Deductions: £{parseFloat(displayResult.totalPreTaxDeductions).toFixed(2)}</p>
                                            )}
                                            <p className="text-sm text-black">- Income Tax: £{parseFloat(displayResult.incomeTax).toFixed(2)}</p>
                                            <p className="text-sm text-black">- National Insurance: £{parseFloat(displayResult.nationalInsurance).toFixed(2)}</p>
                                            {parseFloat(displayResult.studentLoanRepayment) > 0 && (
                                                <p className="text-sm text-black">- Student Loan Repayment: £{parseFloat(displayResult.studentLoanRepayment).toFixed(2)}</p>
                                            )}
                                        </div>
                                    </div>

                                    {/* Net Pay Result */}
                                    <div className="mb-4">
                                        <p className="text-sm text-gray-600 mb-2">Net {displayResult.payPeriod}</p>
                                        <p className="text-3xl font-bold text-green-600">£{parseFloat(displayResult.netAnnual).toFixed(2)}</p>
                                    </div>

                                    {/* Effective Tax Rate */}
                                    <div>
                                        <p className="text-sm text-gray-600">Effective Tax Rate</p>
                                        <p className="text-xl font-semibold text-red-600">{(parseFloat(result.effectiveTaxRate) * 100).toFixed(2)}%</p>
                                    </div>
                                </>
                            );
                        })()}
                    </div>
                )}
            </div>
        </div>
    );
}