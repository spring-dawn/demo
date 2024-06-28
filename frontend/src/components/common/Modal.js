import React from "react";

function Modal(props) {
    const { open, close, header, tab, selectTab } = props;

    return (
        <div className={open ? "openModal modal" : "modal"}>
            {open ? (
                <section className="modal-lg">
                    <header>
                        {tab ? (
                            <ul className="tab">
                                {tab.map((ele) => {
                                    return (
                                        <li
                                            key={ele.name}
                                            onClick={ele.onClick}
                                            className={selectTab == ele.value ? "on" : undefined}
                                        >
                                            {ele.name}
                                        </li>
                                    );
                                })}
                            </ul>
                        ) : (
                            header
                        )}
                        <button className="close" onClick={close}>
                            &times;
                        </button>
                    </header>

                    <main>{props.children}</main>
                </section>
            ) : null}
        </div>
    );
}

export default Modal;
