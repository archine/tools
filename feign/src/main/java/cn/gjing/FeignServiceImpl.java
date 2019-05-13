package cn.gjing;

import feign.Client;
import feign.Contract;
import feign.Feign;
import feign.codec.Decoder;
import feign.codec.Encoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.openfeign.FeignClientsConfiguration;
import org.springframework.cloud.openfeign.ribbon.LoadBalancerFeignClient;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Service;

/**
 * @author Gjing
 **/
@Service
@Import(FeignClientsConfiguration.class)
class FeignServiceImpl implements FeignService {
    private final Feign.Builder urlBuilder;

    private final Feign.Builder nameBuilder;

    @Autowired
    public FeignServiceImpl(Decoder decoder, Encoder encoder, Client client, Contract contract) {
        // nameBuilder直接使用client，它会使用负载均衡
        nameBuilder = Feign.builder()
                .client(client)
                .encoder(encoder)
                .decoder(decoder)
                .contract(contract);
        if (client instanceof LoadBalancerFeignClient) {
            client = ((LoadBalancerFeignClient)client).getDelegate();
        }
        // 无需均衡负载
        urlBuilder = Feign.builder()
                .client(client)
                .encoder(encoder)
                .decoder(decoder)
                .contract(contract);
    }
    @Override
    public <T> T  newInstanceByUrl(Class<T> tClass,String targetServeUrl) {
        return urlBuilder.target(tClass, targetServeUrl);
    }

    @Override
    public <T> T newInstanceByName(Class<T> tClass,String targetServeName) {
        return nameBuilder.target(tClass, targetServeName);
    }

}
