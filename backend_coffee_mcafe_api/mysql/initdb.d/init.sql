create table test_table
(
    id                bigint auto_increment  primary key comment 'ID',
    string_value      varchar(255)                       not null comment '문자',
    long_value        bigint                             null comment 'Long',
    date_time_value   datetime default CURRENT_TIMESTAMP not null comment 'datetime'
)
 comment 'test_table';
