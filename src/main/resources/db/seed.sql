-- Seed data for UK tax years 
--Tax year 2026/27
INSERT INTO tax_year (
    label,
    personal_allowance,
    basic_rate_threshold,
    higher_rate_threshold,
    basic_rate,
    higher_rate,
    additional_rate,
    allowance_withdrawal_threshold,
    allowance_withdrawal_rate
)VALUES (
    '2026/27', --Tax year label
    12570.00, --Personal allowance
    50270.00,  --Basic rate threshold
    125140.00, --Higher rate threshold
    0.20, --Basic rate
    0.40, --Higher rate
    0.45, --Additional rate
    100000.00, --Allowance withdrawal threshold
    0.50 --Allowance withdrawal rate
)ON CONFLICT (label) DO NOTHING; -- Avoid duplicate entries if seed is run multiple times

--Tax year 2025/26
INSERT INTO tax_year (
    label,
    personal_allowance,
    basic_rate_threshold,
    higher_rate_threshold,
    basic_rate,
    higher_rate,
    additional_rate,
    allowance_withdrawal_threshold,
    allowance_withdrawal_rate
)VALUES (
    '2025/26',
    12570.00,
    50270.00,
    125140.00,
    0.20,
    0.40,
    0.45,
    100000.00,
    0.50
)ON CONFLICT (label) DO NOTHING; -- Avoid duplicate entries if seed is run multiple times

--Tax year 2024/25
INSERT INTO tax_year (
    label,
    personal_allowance,
    basic_rate_threshold,
    higher_rate_threshold,
    basic_rate,
    higher_rate,
    additional_rate,
    allowance_withdrawal_threshold,
    allowance_withdrawal_rate
)VALUES (
    '2024/25',
    12570.00,
    50270.00,
    125140.00,
    0.20,
    0.40,
    0.45,
    100000.00,
    0.50
)ON CONFLICT (label) DO NOTHING; -- Avoid duplicate entries if seed is run multiple times