package es.um.dis.tecnomod.oquo.utils;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;

import org.junit.jupiter.api.Test;

class NamespacesTest {

	@Test
	void getPrefixMapTest() {
		Map<String, String> prefixMap = Namespaces.getPrefixMap();
		assertFalse(prefixMap.isEmpty());
		System.out.println(prefixMap);
	}

}
