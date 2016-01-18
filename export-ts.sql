Copy (Select * From vw_ts_person order by person_numb) To 'c:\\york\\ts-gems\\person.csv' With CSV header

Copy (Select * From vw_ts_alt_p order by alt_person_numb) To 'c:\\york\\ts-gems\\alt_p.csv' With CSV header

Copy (Select * From vw_ts_address order by address_number) To 'c:\\york\\ts-gems\\address.csv' With CSV header

Copy (Select * From vw_ts_person_with_master order by person_numb) To 'c:\\york\\ts-gems\\person_with_master.csv' With CSV header

Copy (Select * From vw_ts_person_with_master where is_master=1) To 'c:\\york\\ts-gems\\person_with_master_only.csv' With CSV header

Copy (Select * From vw_ts_person_with_master_alt order by person_numb) To 'c:\\york\\ts-gems\\person_with_master_alt.csv' With CSV header

Copy (Select * From vw_ts_xref_dr ) To 'c:\\york\\ts-gems\\xref_dr.csv' With CSV header

Copy (Select * From vw_ts_holding_current ) To 'c:\\york\\ts-gems\\holding_current.csv' With CSV header

Select count(*) From vw_ts_person
