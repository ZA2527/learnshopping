package com.neuedu.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.neuedu.common.ResponseCode;
import com.neuedu.common.ServerResponse;
import com.neuedu.dao.CategoryMapper;
import com.neuedu.dao.ProductMapper;
import com.neuedu.pojo.Category;
import com.neuedu.pojo.Product;
import com.neuedu.service.ICategoryService;
import com.neuedu.service.IProductService;
import com.neuedu.utils.DateUtils;
import com.neuedu.utils.FTPUtils;
import com.neuedu.utils.PropertiesUtils;
import com.neuedu.vo.ProductDetailVO;
import com.neuedu.vo.ProductListVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Service
public class ProductServiceImpl implements IProductService{

    @Autowired
    ProductMapper productMapper;

    @Autowired
    CategoryMapper categoryMapper;

    @Autowired
    ICategoryService categoryService;

    @Override
    public ServerResponse saveOrUpdate(Product product) {
//        步骤一：参数非空校验
        if(product == null){
            return ServerResponse.createServerResponseByError("参数不能为空");
        }
//        步骤二：设置商品的主图 sub_images --->1.jpg,2.jpg,3.png
        String subImages = product.getSubImages();
        if(subImages!=null && !subImages.equals("")){
            String[] subImageArr = subImages.split(",");
            if(subImageArr.length>0){
//                设置商品主图
                product.setMainImage(subImageArr[0]);
            }
        }
//        步骤三：商品save or update
        if(product.getId() == null){
            //添加
            int result = productMapper.insert(product);
            if(result>0){
                return ServerResponse.createServerResponseBySuccess();
            }else{
                return ServerResponse.createServerResponseByError("添加失败");
            }
        }else{
            //更新
            int result = productMapper.updateByPrimaryKey(product);
            if(result>0){
                return ServerResponse.createServerResponseBySuccess();
            }else{
                return ServerResponse.createServerResponseByError("更新失败");
            }
        }
//        步骤四：返回结果
    }

    @Override
    public ServerResponse set_sale_status(Integer productId, Integer status) {
//        步骤一：参数的非空校验
        if(productId==null){
            return ServerResponse.createServerResponseByError("商品id参数不能为空");
        }
        if(status==null){
            return ServerResponse.createServerResponseByError("商品状态参数不能为空");
        }
//        步骤二：更新商品的状态
        Product product = new Product();
        product.setId(productId);
        product.setStatus(status);
        int result = productMapper.updateProductKeySelective(product);
//        步骤三：返回结果
        if(result>0){
            return ServerResponse.createServerResponseBySuccess();
        }else{
            return ServerResponse.createServerResponseByError("更新失败");
        }
    }

    @Override
    public ServerResponse detail(Integer productId) {
//        步骤一：参数非空效验
        if(productId==null){
            return ServerResponse.createServerResponseByError("商品id参数不能为空");
        }
//        步骤二：根据商品id查询product
        Product product = productMapper.selectByPrimaryKey(productId);
        if(product == null){
            return ServerResponse.createServerResponseByError("商品不存在");
        }
//        步骤三：product --->productDetailVO
        ProductDetailVO productDetailVO = assembleProductDetailVO(product);
//        步骤四：返回结果
        return ServerResponse.createServerResponseBySuccess(null,productDetailVO);
    }

    private ProductDetailVO assembleProductDetailVO(Product product){
        ProductDetailVO productDetailVO = new ProductDetailVO();
        productDetailVO.setCategoryId(product.getCategoryId());
        productDetailVO.setCreateTime(DateUtils.dateToStr(product.getCreateTime()));
        productDetailVO.setDetail(product.getDetail());
        productDetailVO.setImageHost(PropertiesUtils.readByKey("imageHost"));
        productDetailVO.setName(product.getName());
        productDetailVO.setMainImage(product.getMainImage());
        productDetailVO.setId(product.getId());
        productDetailVO.setPrice(product.getPrice());
        productDetailVO.setStatus(product.getStatus());
        productDetailVO.setStock(product.getStock());
        productDetailVO.setSubImages(product.getSubImages());
        productDetailVO.setSubtitle(product.getSubtitle());
        productDetailVO.setUpdateTime(DateUtils.dateToStr(product.getUpdateTime()));
        Category category = categoryMapper.selectByPrimaryKey(product.getCategoryId());
        if(category!=null){
            productDetailVO.setParentCategoryId(category.getParentId());
        }else{
            //默认根节点
            productDetailVO.setParentCategoryId(0);
        }
        return productDetailVO;
    }


    @Override
    public ServerResponse list(Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum,pageSize);
//        步骤一：查询商品数据
//        select * from product limit (pageNum-1)*pageSize,pageSize
        List<Product> productList = productMapper.selectAll();
        List<ProductListVO> productListVOList = Lists.newArrayList();
        if(productList!=null&&productList.size()>0){
            for(Product product:productList){
                ProductListVO productListVO = assembleProductListVO(product);
                productListVOList.add(productListVO);
            }
        }

        PageInfo pageInfo = new PageInfo(productListVOList);

        return ServerResponse.createServerResponseBySuccess(null,pageInfo);
    }

    private ProductListVO assembleProductListVO(Product product){
        ProductListVO productListVO = new ProductListVO();
        productListVO.setId(product.getId());
        productListVO.setCategoryId(product.getCategoryId());
        productListVO.setMainImage(product.getMainImage());
        productListVO.setName(product.getName());
        productListVO.setPrice(product.getPrice());
        productListVO.setStatus(product.getStatus());
        productListVO.setSubtitle(product.getSubtitle());

        return productListVO;
    }

    @Override
    public ServerResponse search(Integer productId, String productName, Integer pageNum, Integer pageSize) {
//         select * from product where productId ? and prodctName like %name%
        PageHelper.startPage(pageNum,pageSize);
        if(productName!=null && !productName.equals("")){
            productName="%"+productName+"%";
        }

        List<Product> productList = productMapper.findProductByProductIdAndProductName(productId,productName);
        List<ProductListVO> productListVOList = Lists.newArrayList();
        if(productList!=null&&productList.size()>0){
            for(Product product:productList){
                ProductListVO productListVO = assembleProductListVO(product);
                productListVOList.add(productListVO);
            }
        }
        PageInfo pageInfo = new PageInfo(productListVOList);

        return ServerResponse.createServerResponseBySuccess(null,pageInfo);
    }

    @Override
    public ServerResponse upload(MultipartFile file, String path) {
//        步骤一：非空判断
            if(file == null){
                return ServerResponse.createServerResponseByError();
            }
//            步骤二：获取图片的名字
        String orignalFileName = file.getOriginalFilename();
        //获取图片的扩展名
        String exName = orignalFileName.substring(orignalFileName.lastIndexOf("."));  //.jpg
        //为图片生成新的唯一的名字
        String newFileName = UUID.randomUUID().toString()+exName;
        File pathFile = new File(path);
        if(!pathFile.exists()){
            pathFile.setWritable(true);
            pathFile.mkdir();
        }

        File file1 = new File(path,newFileName);
        try {
            file.transferTo(file1);
            //上传到图片服务器
            FTPUtils.uploadFile(Lists.newArrayList(file1));
            //。。。。。。。。
            Map<String,String> map = Maps.newHashMap();
            map.put("uri",newFileName);
            map.put("url",PropertiesUtils.readByKey("imageHost")+"/"+newFileName);

            //删除应用服务器上的图片
            file1.delete();

            return ServerResponse.createServerResponseBySuccess(null,map);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    //前台接口--商品详细
    @Override
    public ServerResponse detail_portal(Integer productId) {
//        步骤一：参数非空效验
        if(productId==null){
            return ServerResponse.createServerResponseByError("商品id参数不能为空");
        }
//        步骤二：根据商品id查询product
        Product product = productMapper.selectByPrimaryKey(productId);
        if(product == null){
            return ServerResponse.createServerResponseByError("商品不存在");
        }
//        步骤三：校验商品状态
        if(product.getStatus()!=ResponseCode.ProductStatusEnum.PRODUCT_ONLINE.getCode()){
            return ServerResponse.createServerResponseByError("商品已下架或删除");
        }
//        步骤四：获取productDetailVO
        ProductDetailVO productDetailVO = assembleProductDetailVO(product);
//        步骤五：返回结果

        return ServerResponse.createServerResponseBySuccess(null,productDetailVO);
    }

    @Override
    public ServerResponse list_portal(Integer categoryId, String keyword, Integer pageNum, Integer pageSize, String orderBy) {

//        步骤一：参数校验categoryId和keyword不能同时为空
        if(categoryId==null && (keyword==null || keyword.equals(""))){
            return ServerResponse.createServerResponseByError("参数错误");
        }
//        步骤二：根据categoryId查询
        Set<Integer> integerSet =Sets.newHashSet();
        if(categoryId!=null){
            Category category = categoryMapper.selectByPrimaryKey(categoryId);
            if(category == null && (keyword==null || keyword.equals(""))){
                //说明没有商品数据
                PageHelper.startPage(pageNum,pageSize);
                List<ProductListVO> productListVOList = Lists.newArrayList();
                PageInfo pageInfo = new PageInfo(productListVOList);
                return ServerResponse.createServerResponseBySuccess(null,pageInfo);
            }
            ServerResponse serverResponse = categoryService.get_deep_category(categoryId);
            if(serverResponse.isSuccess()){
                integerSet = (Set<Integer>)serverResponse.getData();

            }
        }
//        步骤三：根据keyword查询
        if(keyword!=null && keyword.equals("")){
            keyword="%"+keyword+"%";
        }
        if(orderBy.equals("")){
            PageHelper.startPage(pageNum,pageSize);
        }else{
            String[] orderByArr = orderBy.split("_");
            if(orderByArr.length>1){
                PageHelper.startPage(pageNum,pageSize,orderByArr[0]+" "+orderByArr[1]);
            }else{
                PageHelper.startPage(pageNum,pageSize);
            }
        }
//        步骤四：List<Product> --->  List<ProductListVO>
        List<Product> productList = productMapper.searchProduct(integerSet,keyword);
        List<ProductListVO> productListVOList = Lists.newArrayList();
        if(productList!=null && productList.size()>0){
            for(Product product:productList){
                ProductListVO productListVO = assembleProductListVO(product);
                productListVOList.add(productListVO);
            }
        }
//        步骤五：分页
        PageInfo pageInfo = new PageInfo(productListVOList);
//        步骤六：返回结果

        return ServerResponse.createServerResponseBySuccess(null,pageInfo);
    }

}
