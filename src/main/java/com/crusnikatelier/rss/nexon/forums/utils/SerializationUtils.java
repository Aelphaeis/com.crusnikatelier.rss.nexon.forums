package com.crusnikatelier.rss.nexon.forums.utils;

import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

public class SerializationUtils {
	
	public static Document toDocument(Object obj){
		
		Marshaller marshaller = null;
		Document document = null;
		try{
			JAXBContext context = JAXBContext.newInstance(obj.getClass());
			marshaller = context.createMarshaller();
			
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			document = db.newDocument();
			
			marshaller.marshal(obj, document);
			
			return document;
		}
		catch (JAXBException e) {
			//Should really not occur since we control all objects
			throw new IllegalStateException(e);
		} 
		catch (ParserConfigurationException e) {
			//Should not occur since we control the Object we're writing
			throw new IllegalStateException(e);
		}
	}
	
	public static String toString(Object current){
		try{
			TransformerFactory transFactory = TransformerFactory.newInstance();
			Transformer transformer = transFactory.newTransformer();
			StringWriter buffer = new StringWriter();
			Node n = toDocument(current);
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			
			transformer.transform(new DOMSource(n), new StreamResult(buffer));
			return buffer.toString();
		}
		catch(Throwable t){
			throw new RuntimeException(t);
		}
	}
	
}
