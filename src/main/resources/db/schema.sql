CREATE TABLE IF NOT EXISTS tax_year (
    id                              BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    label                           VARCHAR(32) NOT NULL UNIQUE,
    personal_allowance              DECIMAL(12,2) NOT NULL,
    basic_rate_threshold            DECIMAL(12,2) NOT NULL,
    higher_rate_threshold           DECIMAL(12,2) NOT NULL,
    basic_rate                      DECIMAL(5,4) NOT NULL,
    higher_rate                     DECIMAL(5,4) NOT NULL,
    additional_rate                 DECIMAL(5,4) NOT NULL,
    allowance_withdrawal_threshold  DECIMAL(12,2) NOT NULL,
    allowance_withdrawal_rate       DECIMAL(5,4) NOT NULL
);