drop function if exists   insert_sys_operationlog_data ;
create function insert_sys_operationlog_data()
 returns  int deterministic
    begin
    declare  i int;
      set i=1;
      while  i<=1000 do 
      insert into sys_operationlog (logid,syscode,modulecode,menucode)  values(i , concat('syscode',i),floor(rand()*1000),concat('menucode',i) );
      set i=i+1;
      end while;
      return 1;
    end;
select  insert_sys_operationlog_data();
commit;