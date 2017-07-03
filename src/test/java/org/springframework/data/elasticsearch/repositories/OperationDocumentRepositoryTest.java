package org.springframework.data.elasticsearch.repositories;

import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.entities.Contact;
import org.springframework.data.elasticsearch.entities.OperationDocument;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:/springContext-test.xml") //old: spring-context.xml
public class OperationDocumentRepositoryTest {

	@Autowired
	private ElasticsearchTemplate elasticsearchTemplate;

	@Autowired
	private OperationDocumentRepository operationRepository;

//	@Before
//	public void emptyData(){
//		operationRepository.deleteAll();
//	}
//	@Before
//	public void before() {
//		elasticsearchTemplate.deleteIndex(OperationDocument.class);
//		elasticsearchTemplate.createIndex(OperationDocument.class);
//		elasticsearchTemplate.putMapping(OperationDocument.class);
//	}

	@Test
	public void test() {
		Map<?, ?> mapping = elasticsearchTemplate.getMapping(OperationDocument.class);
		System.out.println(mapping.toString());

//		System.out.println("mapping.get(properties)" + mapping.get("properties"));
		Map<?, ?> mappedProperties = (Map<?, ?>)mapping.get("properties");

		System.out.println("mappedProperties.someTransientData = " + mappedProperties.get("someTransientData"));
/*
{properties={dateUp={type=date, store=true, format=dd.MM.yyyy hh:mm}, operationName={type=string, store=true, analyzer=standard}, sectors={type=nested}, someTransientData={type=string, index=not_analyzed}}}
mappedProperties.someTransientData = {type=string, index=not_analyzed}
 */
	}

}