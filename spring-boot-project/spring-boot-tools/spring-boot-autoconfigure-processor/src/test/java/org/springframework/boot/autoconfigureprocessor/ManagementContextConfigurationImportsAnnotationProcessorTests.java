/*
 * Copyright 2012-2022 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.boot.autoconfigureprocessor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import org.springframework.boot.testsupport.compiler.TestCompiler;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link ManagementContextConfigurationImportsAnnotationProcessor}.
 *
 * @author Scott Frederick
 */
class ManagementContextConfigurationImportsAnnotationProcessorTests {

	@TempDir
	File tempDir;

	private TestCompiler compiler;

	@BeforeEach
	void createCompiler() throws IOException {
		this.compiler = new TestCompiler(this.tempDir);
	}

	@Test
	void annotatedClasses() throws Exception {
		List<String> classes = compile(TestManagementContextConfigurationTwo.class,
				TestManagementContextConfigurationOne.class);
		assertThat(classes).hasSize(2);
		assertThat(classes).containsExactly(
				"org.springframework.boot.autoconfigureprocessor.TestManagementContextConfigurationOne",
				"org.springframework.boot.autoconfigureprocessor.TestManagementContextConfigurationTwo");
	}

	@Test
	void notAnnotatedClasses() throws Exception {
		List<String> classes = compile(TestAutoConfigurationConfiguration.class);
		assertThat(classes).isNull();
	}

	private List<String> compile(Class<?>... types) throws IOException {
		TestManagementContextConfigurationImportsAnnotationProcessor processor = new TestManagementContextConfigurationImportsAnnotationProcessor();
		this.compiler.getTask(types).call(processor);
		return getWrittenImports(processor.getImportsFilePath());
	}

	private List<String> getWrittenImports(String importsFilePath) throws IOException {
		File file = new File(this.tempDir, importsFilePath);
		if (!file.exists()) {
			return null;
		}
		BufferedReader reader = new BufferedReader(new FileReader(file));
		return reader.lines().collect(Collectors.toList());
	}

}
