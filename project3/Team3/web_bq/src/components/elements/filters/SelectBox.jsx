function SelectBox({
                       id,
                       title,
                       fontSize = "18px",
                       width = "180px",
                       margin = "10px",
                       options = [],
                       setState,
                       disabled = false,
                       textColor = "black",
                       value
                   }) {
    const styles = {color: textColor, width: width, backgroundColor: disabled ? "#E5E5E5" : "white"};

    return (
        <div className={"d-flex justify-content-center align-items-center input"}>
            <div className={"selectBoxText"} style={{marginRight: margin, fontSize: fontSize}}>{title}</div>
            <select id={id} style={styles} className={"selectBox"} value={value} onChange={(e) => setState(e.target.value)}
                    disabled={disabled}>
                {options.map((opt) => (
                    <option key={opt.value} value={opt.value}>{opt.label}</option>
                ))}
            </select>
        </div>
    );
}

export default SelectBox