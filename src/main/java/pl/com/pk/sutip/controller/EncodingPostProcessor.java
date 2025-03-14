package pl.com.pk.sutip.controller;

import java.nio.charset.Charset;
import java.util.Arrays;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.servlet.mvc.annotation.AnnotationMethodHandlerAdapter;

/*
 *  Klasa odpowiedzialna za kodowanie @ResponseBody lub ResponseEntity<String>, nagłówkek odpowiedzi powinie teraz
 *  zawierać UTF-8, a nie ISO-1, obecnie klasa nie jest wykorzystywana!
 */
public class EncodingPostProcessor implements BeanPostProcessor
{
    public Object postProcessBeforeInitialization(Object bean, String name) throws BeansException
    {
        if (bean instanceof AnnotationMethodHandlerAdapter)
        {
            HttpMessageConverter<?>[] convs = ((AnnotationMethodHandlerAdapter) bean).getMessageConverters();
            for (HttpMessageConverter<?> conv: convs) 
            {
                if (conv instanceof StringHttpMessageConverter) 
                {
                    ((StringHttpMessageConverter) conv).setSupportedMediaTypes(Arrays.asList(new MediaType("text", "html", Charset.forName("UTF-8"))));
                }
            }
        }
        return bean;
    }

    public Object postProcessAfterInitialization(Object bean, String name) throws BeansException
    {
        return bean;
    }
}
