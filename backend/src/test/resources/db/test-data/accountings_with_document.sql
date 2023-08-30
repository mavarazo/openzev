INSERT INTO PUBLIC.OZEV_DOCUMENTS(id, "uuid", created, modified, data)
VALUES (999,
        '86fb361f-a577-405e-af02-f524478d2e49',
        '2023-06-01',
        null,
        'a document'::bytea);


INSERT INTO PUBLIC.OZEV_ACCOUNTINGS(id, "uuid", created, modified, agreement_id, period_from, period_upto, subject,
                                    amount_ht, amount_lt, amount_total, document_id)
VALUES (999,
        '86fb361f-a577-405e-af02-f524478d2e49',
        '2023-06-01',
        null,
        999,
        '2023-01-01',
        '2023-12-31',
        'Abrechnung 2023',
        '100.00',
        '75.00',
        '175.00',
        999);
