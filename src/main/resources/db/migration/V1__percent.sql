create function hemmingmatchpercent(hash1 bigint, hash2 bigint) returns real
    language plpgsql
as
$$
declare
    hashesXor bigint := hash1 # hash2;
    numberOfOne bigint := 0;
    resultVar real := 0.0;
begin
    if (hashesXor < 0) then
        hashesXor := ~hashesXor;
    end if;

    while(hashesXor > 0)
        loop
            numberOfOne := numberOfOne + (hashesXor & 1);
            hashesXor := hashesXor >> 1;
        end loop;

    resultVar := 1 - cast(numberOfOne as double precision)/64;
    return resultVar;
end;
$$;

alter function hemmingmatchpercent(bigint, bigint) owner to postgres;

