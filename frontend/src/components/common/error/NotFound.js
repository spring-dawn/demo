function NotFound(props) {
    const { errStatus, errMsg } = props;

    // 로그인을 안 했거나 권한이 없으면 401, 없는 페이지면 404?
    const onRedirect = () => {
        window.location.href = "/";
    };

    return (
        <div className="errWrap" style={{ width: "600px", margin: "200px auto" }}>
            <div className="errPage" style={{ display: "flex", flexDirection: "column", alignItems: "center" }}>
                <div className="errInfo">
                    <h1>{errStatus}</h1>
                </div>
                <div>{errMsg}</div>

                <div className="redirect2Main" style={{ marginTop: "20px" }}>
                    <button className="btn" onClick={onRedirect}>
                        메인으로
                    </button>
                </div>
            </div>
        </div>
    );
}

export default NotFound;
