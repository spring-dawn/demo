import React, { useEffect, useState } from "react";
import { useForm } from "react-hook-form";
import { useRecoilState } from "recoil";
import { SearchState } from "../../Context";
import moment from "moment";

function SearchMulti(props) {
    const [searchState, setSearchState] = useRecoilState(SearchState);
    const { list, setData, url, render } = props;
    const {
        handleSubmit,
        register,
        getValues,
        trigger,
        formState: { errors },
    } = useForm();

    const startYear = 2020;
    const endYear = moment().format("yyyy");
    const years = Array.from({ length: endYear - startYear + 1 }, (_, index) => startYear + index);
    const yearOptions = [
        { name: "전체", value: "" },
        ...years.map((year) => {
            return {
                name: year,
                value: year,
            };
        }),
    ];
    const monthOptions = [
        { name: "전체", value: "" },
        { name: "01", value: "01" },
        { name: "02", value: "02" },
        { name: "03", value: "03" },
        { name: "04", value: "04" },
        { name: "05", value: "05" },
        { name: "06", value: "06" },
        { name: "07", value: "07" },
        { name: "08", value: "08" },
        { name: "09", value: "09" },
        { name: "10", value: "10" },
        { name: "11", value: "11" },
        { name: "12", value: "12" },
    ];

    const onSubmit = (data) => {
        let str = "?";

        for (let key in data) {
            str += data[key] !== "" ? `${key}=${data[key]}&` : "";
        }

        let newUrl = str !== "" ? url + str.slice(0, str.length - 1) : url;
        // data: 검색 파라미터

        fetch(newUrl)
            .then((res) => {
                if (res.ok === false) {
                    return;
                } else {
                    return res.json();
                }
            })
            .then((data) => {
                if (data === undefined) {
                    setData([]);
                } else {
                    setData(data);
                }
            });

        setSearchState(getValues());
    };

    useEffect(() => {
        document.querySelector("button").click();
    }, [render]);

    return (
        <form className="search_form" id="serachForm" onSubmit={handleSubmit(onSubmit)}>
            <div className="multiSearchWrap">
                <ul className="_row">
                    {list.map((li) => {
                        return (
                            <li key={li.id} className={li.col ? `_col_gap _col${li.col}` : null}>
                                <div className="form_group" key={li.id}>
                                    <label id={li.id}>{li.label}</label>
                                    <div className="input_group">
                                        {li.type === "select" ? (
                                            <select
                                                htmlFor={li.id}
                                                className="form_control"
                                                {...register(
                                                    li.id,
                                                    li.required ? { required: "필수 입력 값 입니다." } : ""
                                                )}
                                            >
                                                {li.option.map((m) => {
                                                    return (
                                                        <option key={m.name} value={m.value}>
                                                            {m.name}
                                                        </option>
                                                    );
                                                })}
                                            </select>
                                        ) : li.type === "selectYear" ? (
                                            <select
                                                htmlFor={li.id}
                                                className="form_control"
                                                {...register(
                                                    li.id,
                                                    li.required ? { required: "필수 입력 값 입니다." } : ""
                                                )}
                                            >
                                                {yearOptions.map((m) => {
                                                    return (
                                                        <option key={m.name} value={m.value}>
                                                            {m.name}
                                                        </option>
                                                    );
                                                })}
                                            </select>
                                        ) : li.type === "selectMonth" ? (
                                            <select
                                                htmlFor={li.id}
                                                className="form_control"
                                                {...register(
                                                    li.id,
                                                    li.required ? { required: "필수 입력 값 입니다." } : ""
                                                )}
                                            >
                                                {monthOptions.map((m) => {
                                                    return (
                                                        <option key={m.name} value={m.value}>
                                                            {m.name}
                                                        </option>
                                                    );
                                                })}
                                            </select>
                                        ) : (
                                            <>
                                                <input
                                                    htmlFor={li.id}
                                                    className="form_control"
                                                    {...register(
                                                        li.id,
                                                        li.required ? { required: "필수 입력 값 입니다." } : ""
                                                    )}
                                                    type={li.type2 ? li.type2 : ""}
                                                    placeholder={li.placeholder ? li.placeholder : ""}
                                                />
                                                {li.required ? (
                                                    <span className={errors[li.id] !== undefined ? "help_block" : ""}>
                                                        {errors[li.id]?.message}
                                                    </span>
                                                ) : null}
                                            </>
                                        )}
                                    </div>
                                </div>
                            </li>
                        );
                    })}
                </ul>

                <button className="btn" id="btn_search" type="submit">
                    검색
                </button>
            </div>
        </form>
    );
}

export default SearchMulti;
