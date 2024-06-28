import React, { useEffect, useRef } from "react";

function ObjectDuplInfo({ setObjectDuplInfoRef, objectDuplInfoCol, setObjectDuplInfoCol, setSelectLayer }) {
    const ref = useRef(null); // 인포창 dom

    useEffect(() => {
        setObjectDuplInfoRef(ref);
    }, [ref]);

    return (
        <ul id="objectDuplInfo" ref={ref}>
            <div className={"header"}>중첩 레이어</div>
            <ul className="scroll">
                {objectDuplInfoCol.map((duplCol) => {
                    const key = duplCol.find((col) => col.key);

                    return (
                        <li
                            key={key.keyName}
                            onClick={() => {
                                setObjectDuplInfoCol([]);
                                ref.current.style.display = "none";
                                setSelectLayer([duplCol[0].selectData]);
                            }}
                        >
                            {key.keyName}
                        </li>
                    );
                })}
            </ul>
        </ul>
    );
}

export default ObjectDuplInfo;
