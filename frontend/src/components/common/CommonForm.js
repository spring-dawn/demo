import React, { useEffect, useState } from "react";
import { useLocation } from "react-router-dom";
import { useForm } from "react-hook-form";

function CommonForm(props) {
    const location = useLocation();

    const { list, mode, data, close, form, setDel, chkFn } = props;

    // PfSub Id Setting
    const targetId = {
        user: "userId",
        code: "id",
    };

    const {
        handleSubmit,
        register,
        formState: { errors },
        reset,
        getValues,
        setValue,
    } = useForm();

    useEffect(() => {
        if (data) {
            reset(data);
        }
    }, [data, reset]);

    // Input Value Change
    const [change, setChange] = useState();
    useEffect(() => {
        if (change !== undefined && change.target.id === "userId") {
            chkFn(false);
        }
    }, [change]);

    // Save
    const onSubmit = (data) => {
        form(data);
    };

    // Delete
    const target = location.pathname.split("/")[location.pathname.split("/").length - 1];
    const btnDelete = (e) => {
        e.preventDefault();

        let id = getValues(targetId[target]);
        setDel({ id: id, del: true });
    };

    return (
        <form onSubmit={handleSubmit(onSubmit)}>
            <div className="modal_body">
                <ul className="_row">
                    {list.length !== 0 &&
                        list.map((li) => {
                            return (
                                <li key={li.id} className={li.col ? `_col_gap _col${li.col}` : null}>
                                    <div className="form_group">
                                        {li.type !== "button" ? (
                                            <label htmlFor={li.id}>
                                                {li.label}
                                                {li.required ? <span>(*)</span> : null}
                                            </label>
                                        ) : null}
                                        <div className="input_group">
                                            {li.type === "input" ? (
                                                <>
                                                    <input
                                                        type={li.input_type}
                                                        id={li.id}
                                                        className="form_control"
                                                        {...register(li.id, {
                                                            required: li.msg,
                                                            onChange: (e) => setChange(e),
                                                        })}
                                                        readOnly={
                                                            li.readonly !== undefined && li.readonly ? "readonly" : null
                                                        }
                                                    />
                                                    <span className={errors[li.id] !== undefined ? "help_block" : ""}>
                                                        {errors[li.id]?.message}
                                                    </span>
                                                </>
                                            ) : li.type === "select" ? (
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
                                                                {m.label}
                                                            </option>
                                                        );
                                                    })}
                                                </select>
                                            ) : li.type === "textarea" ? (
                                                <textarea
                                                    id={li.id}
                                                    rows="3"
                                                    className="form_control"
                                                    {...register(li.id)}
                                                ></textarea>
                                            ) : li.type === "button" ? (
                                                <button className={li.class} onClick={li.evt}>
                                                    {li.label}
                                                </button>
                                            ) : null}
                                        </div>
                                    </div>
                                </li>
                            );
                        })}
                </ul>
            </div>

            <footer>
                <div className="btnWrap">
                    {mode === "update" ? (
                        <button className="btn btn_delete" onClick={btnDelete}>
                            삭제
                        </button>
                    ) : null}
                    <button className="btn btn_close" onClick={close}>
                        닫기
                    </button>
                    <button className="btn btn_save" type="submit">
                        {mode === "update" ? "수정" : "등록"}
                    </button>
                </div>
            </footer>
        </form>
    );
}

export default CommonForm;
