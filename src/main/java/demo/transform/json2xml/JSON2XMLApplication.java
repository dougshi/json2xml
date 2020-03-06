package demo.transform.json2xml;

import java.io.IOException;
import java.time.OffsetDateTime;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.apache.camel.CamelExecutionException;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.jackson.JacksonDataFormat;
import org.apache.camel.converter.jaxb.JaxbDataFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import io.github.threetenjaxb.core.OffsetDateTimeXmlAdapter;
import lombok.Data;

@SpringBootApplication
@RestController
public class JSON2XMLApplication {

	public static void main(String[] args) {
		SpringApplication.run(JSON2XMLApplication.class, args);
	}
	
	@Autowired
	ProducerTemplate producerTemplate;
	
	@PostMapping(path="/xml", produces="application/xml")
	public String convertToXml(@RequestParam(name="file") MultipartFile jsonInput) throws CamelExecutionException, IOException {
		Assert.notNull(jsonInput, "Must have Project Json");
		return (String)producerTemplate.requestBody("direct:toXml", new String(jsonInput.getBytes()));
	}
	
	@PostMapping(path="/json", produces="application/json")
	public String convertToJson(@RequestParam(name="file") MultipartFile xmlInput) throws CamelExecutionException, IOException {
		Assert.notNull(xmlInput, "Must have Project XML");
		return (String)producerTemplate.requestBody("direct:toJson", new String(xmlInput.getBytes()));
	}
	

	@Component
	public class XmlServiceRoute extends RouteBuilder {

		@Override
		public void configure() throws Exception {

			JaxbDataFormat jaxbFormat = createJaxbFormat(Project.class);
			JacksonDataFormat jsonFormat = createJsonFormat(Project.class);
			
			from("direct:toXml").routeId("toXml")
				.unmarshal(jsonFormat)
				.marshal(jaxbFormat)
				.convertBodyTo(String.class);
			
			from("direct:toJson").routeId("toJson")
				.unmarshal(jaxbFormat)
				.marshal(jsonFormat)
				.convertBodyTo(String.class);

		}
	};

	
	protected <T> JaxbDataFormat createJaxbFormat(Class<T> clazz) throws JAXBException{
        return new JaxbDataFormat(JAXBContext.newInstance(clazz));
    }
	
	protected <T> JacksonDataFormat createJsonFormat(Class<T> clazz){
        JacksonDataFormat jacksonDataFormat = new JacksonDataFormat(clazz);

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(MapperFeature.USE_ANNOTATIONS, false);
        mapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
        mapper.setDefaultPropertyInclusion(JsonInclude.Include.NON_EMPTY);
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        jacksonDataFormat.setObjectMapper(mapper);
        
        jacksonDataFormat.addModule(new JavaTimeModule());
        jacksonDataFormat.setPrettyPrint(true);
        //jacksonDataFormat.enableFeature(SerializationFeature.WRAP_ROOT_VALUE);
        //jacksonDataFormat.enableFeature(DeserializationFeature.UNWRAP_ROOT_VALUE);
        
        return jacksonDataFormat;
    }
}

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "Project")
@Data
class Project {
	String name;
	String description;
	@XmlJavaTypeAdapter(OffsetDateTimeXmlAdapter.class)
	@XmlSchemaType(name = "dateTime")
	OffsetDateTime dtAdded;
	
}
