package com.leyou.common.enums;


/**
 * 定义异常信息
 */
public enum ExceptionEnum {


    PRICE_CANNOT_BE_NULL(400,"价格不能为空"),
    CATEGORY_NOT_FOUND(404,"商品分类没查到"),
    BRAND_NOT_FOUND(404,"品牌没有找到" ),
    BRAND_ADD_ERROR(404,"新增品牌失败" ),
    IMAGE_UPLOAD_ERROE(500,"文件上传失败" ),
    INVALID_FILE_TYPE(400,"无效的文件类型"),
    SPEC_GROUP_NOT_FOUND(404,"商品规格组不存在" ),
    SPEC_PARAM_NOT_FOUND(404,"商品规格参数不存在" ),
    GOODS_SKU_NOT_FOUND(404,"商品信息没找到" ),
    GOODS_ADD_ERROE(500,"新增商品失败" ),
    GOODS_STOCK_FOUND_ERROR(404,"商品库存查询失败" ), GOODS_UPDATE_ERROR(400,"更新商品失败" ),
    GOODS_ID_CANNOT_BE_NULL(400,"商品id不能为空" ), GOODS_SPU_FOUND_ERROR(400,"查询商品spu失败" );


    private int code;
    private String msg;

    ExceptionEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
