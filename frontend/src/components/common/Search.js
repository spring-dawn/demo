import React, { useEffect, useState } from "react";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faSearch } from "@fortawesome/free-solid-svg-icons";

function Search(props) {
    const [val, setVal] = useState("");
    const onChangeVal = (e) => {
        setVal(e.target.value);
    };
    const [selected, setSelected] = useState("");
    const onChangeType = (e) => {
        setSelected(e.target.value);
    };

    const onClickSear = () => {
        let searObj = { type: selected, value: val };
        props.fn(searObj);
    };

    return (
        <div className="searchWrap">
            <select className="form_control" onChange={onChangeType} value={selected}>
                {props.option !== undefined &&
                    props.option.map((opt) => {
                        return (
                            <option key={opt.value} value={opt.value}>
                                {opt.name}
                            </option>
                        );
                    })}
            </select>
            <label className="searchArea">
                <input
                    type="text"
                    className="form_control"
                    placeholder="검색어를 입력하세요"
                    onChange={onChangeVal}
                    value={val}
                />
                <button className="btn btn_search" onClick={onClickSear}>
                    <FontAwesomeIcon icon={faSearch} />
                </button>
            </label>
        </div>
    );
}

export default Search;
