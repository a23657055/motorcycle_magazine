package org.iii.SecBuzzer.template.vo;

public class WangEditorVO {
    private Integer errno;
    private Object data;

    public Integer getErrno() {
        return errno;
    }

    public Object getData() {
        return data;
    }

    public static WangEditorVO success(Object data) {
        WangEditorVO wangEditorVO = new WangEditorVO();
        wangEditorVO.errno = 0;
        wangEditorVO.data = data;
        return wangEditorVO;
    }

    public static WangEditorVO error(Integer errno, Object data) {
        WangEditorVO wangEditorVO = new WangEditorVO();
        wangEditorVO.errno = errno;
        wangEditorVO.data = data;
        return wangEditorVO;
    }
}
