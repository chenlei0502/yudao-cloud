package cn.iocoder.mall.product.convert;

import cn.iocoder.common.framework.util.StringUtil;
import cn.iocoder.mall.product.api.bo.*;
import cn.iocoder.mall.product.api.dto.ProductSkuAddOrUpdateDTO;
import cn.iocoder.mall.product.api.dto.ProductSpuAddDTO;
import cn.iocoder.mall.product.api.dto.ProductSpuUpdateDTO;
import cn.iocoder.mall.product.dataobject.ProductCategoryDO;
import cn.iocoder.mall.product.dataobject.ProductSkuDO;
import cn.iocoder.mall.product.dataobject.ProductSpuDO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Mapper
public interface ProductSpuConvert {

    ProductSpuConvert INSTANCE = Mappers.getMapper(ProductSpuConvert.class);

    @Mappings({
            @Mapping(source = "picUrls", target = "picUrls", qualifiedByName = "translatePicUrlsFromString")
    })
    ProductSpuBO convert(ProductSpuDO spu);

    @Named("translatePicUrlsFromString")
    default List<String> translatePicUrlsFromString(String picUrls) {
        return StringUtil.split(picUrls, ",");
    }

    @Mappings({})
    List<ProductSpuBO> convert(List<ProductSpuDO> spus);

    @Mappings({
            @Mapping(source = "picUrls", target = "picUrls", ignore = true)
    })
    ProductSpuDO convert(ProductSpuAddDTO productSpuAddDTO);

    @Mappings({
            @Mapping(source = "attrs", target = "attrs", ignore = true)
    })
    ProductSkuDO convert(ProductSkuAddOrUpdateDTO productSkuAddDTO);


    @Mappings({
            @Mapping(source = "picUrls", target = "picUrls", ignore = true)
    })
    ProductSpuDO convert(ProductSpuUpdateDTO productSpuUpdateDTO);

    @Mappings({})
    ProductSpuDetailBO convert(ProductSpuBO spu);

    @Mappings({
            @Mapping(source = "picUrls", target = "picUrls", ignore = true)
    })
    ProductSpuDetailBO convert2(ProductSpuDO spu);

    @Mappings({
            @Mapping(source = "picUrls", target = "picUrls", ignore = true)
    })
    ProductSkuDetailBO.Spu convert3(ProductSpuDO spu);

    @Mappings({
            @Mapping(source = "attrs", target = "attrs", ignore = true)
    })
    ProductSpuDetailBO.Sku convert2(ProductSkuDO sku);

    @Mappings({
            @Mapping(source = "attrs", target = "attrs", ignore = true)
    })
    ProductSkuDetailBO convert3(ProductSkuDO sku);

    @Mappings({
//            @Mapping(source = "attrs", target = "attrs", ignore = true) // TODO ?????? ????????????
    })
    ProductSkuBO convert4(ProductSkuDO sku);



    @Mappings({}) // TODO ???????????????????????? mapstruct ??? API ???????????????
    default List<ProductSkuDetailBO> convert3(List<ProductSkuDO> skus, List<ProductSpuDO> spus, List<ProductAttrAndValuePairBO> productAttrDetailBOs) {
        // ?????? ProductAttrDetailBO ?????????????????????KEY ??? ProductAttrDetailBO.attrValueId ????????????????????????
        Map<Integer, ProductAttrAndValuePairBO> productAttrDetailBOMap = productAttrDetailBOs.stream().collect(
                Collectors.toMap(ProductAttrAndValuePairBO::getAttrValueId, productAttrDetailBO -> productAttrDetailBO));
        // ?????? ProductSpuDO ?????????
        Map<Integer, ProductSkuDetailBO.Spu> spuMap = spus.stream().collect(
                Collectors.toMap(ProductSpuDO::getId, spu -> ProductSpuConvert.this.convert3(spu).setPicUrls(StringUtil.split(spu.getPicUrls(), ","))));
        // ????????????
        List<ProductSkuDetailBO> spuDetailList = new ArrayList<>(skus.size());
        for (ProductSkuDO sku : skus) {
            // ?????? ProductSkuDetailBO ??????
            ProductSkuDetailBO skuDetail = ProductSpuConvert.this.convert3(sku)
                    .setAttrs(new ArrayList<>())
                    .setSpu(spuMap.get(sku.getSpuId()));
            spuDetailList.add(skuDetail);
            // ?????? ProductSpuDetailBO ??? attrs ????????????
            List<String> attrs = StringUtil.split(sku.getAttrs(), ",");
            attrs.forEach(attr -> skuDetail.getAttrs().add(productAttrDetailBOMap.get(Integer.valueOf(attr))));
        }
        // ??????
        return spuDetailList;
    }



}
