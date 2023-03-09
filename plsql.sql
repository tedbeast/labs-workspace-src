--drop table if exists EMPLOYEE;
--drop table if exists w2;
--drop PROCEDURE if exists add_employee;
--drop FUNCTION if exists generate_w2;
-- create
CREATE TABLE EMPLOYEE (
  empId INTEGER PRIMARY KEY,
  name TEXT NOT NULL,
  dept TEXT NOT NULL,
  time_added timestamp
);

create table w2 (
  w2id INTEGER primary key,
  empId INTEGER, 
  foreign key (empId) references EMPLOYEE(empId)
);

-- fetch 
SELECT * FROM EMPLOYEE WHERE dept = 'Sales';

CREATE PROCEDURE add_employee ()
LANGUAGE SQL
AS $$
INSERT INTO EMPLOYEE (empId, name, dept, time_added) 
VALUES (0004, 'Alice', 'Sales', CURRENT_TIMESTAMP);
$$;

create FUNCTION generate_w2 () returns trigger
AS $$
BEGIN
INSERT INTO w2 (w2id, empId) values (new.empId, new.empId);
RETURN NEW;
END
$$ language plpgsql;


create trigger onEmployeeAddCreateW2 after insert on EMPLOYEE 
FOR EACH ROW
execute FUNCTION generate_w2();

call add_employee();
select * from employee;
select * from w2;
