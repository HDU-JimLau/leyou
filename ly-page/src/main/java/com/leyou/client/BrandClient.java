package com.leyou.client;

import com.leyou.api.BrandsApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(value = "item-service")
public interface BrandClient extends BrandsApi{
}
