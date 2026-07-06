package com.adlph.internal.managment.index.server.entity;

import com.adlph.internal.managment.index.server.data.entity.Department;
import com.adlph.internal.managment.index.server.data.entity.Division;
import com.adlph.internal.managment.index.server.data.entity.Product;
import com.adlph.internal.managment.index.server.data.entity.Project;
import com.adlph.internal.managment.index.server.exception.InvalidDataException;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EntityValidationTest {

    @Nested
    class DivisionValidation {
        @Test
        void valid() {
            var d = Division.builder().name("Test Division").build();
            assertDoesNotThrow(d::validateData);
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {" ", "  ", "\t", "\n"})
        void invalidName(String name) {
            var d = Division.builder().name(name).build();
            var ex = assertThrows(InvalidDataException.class, d::validateData);
            assertEquals("Division name is required", ex.getMessage());
        }

        @Test
        void nameWithSpecialChars() {
            var d = Division.builder().name("División ñoña & Cía. <test> \"OK\"").build();
            assertDoesNotThrow(d::validateData);
        }

        @Test
        void nameMaxLength() {
            var d = Division.builder().name("a".repeat(100)).build();
            assertDoesNotThrow(d::validateData);
        }

        @Test
        void idNullOnCreate() {
            var d = Division.builder().name("OK").build();
            assertNull(d.getId());
        }

        @Test
        void settersAndGetters() {
            var d = new Division();
            d.setId(42L);
            d.setName("Setters");
            assertEquals(42L, d.getId());
            assertEquals("Setters", d.getName());
        }

        @Test
        void allArgsConstructor() {
            var d = new Division(1L, "Test", null);
            assertEquals(1L, d.getId());
            assertEquals("Test", d.getName());
        }

        @Test
        void noArgsConstructor() {
            var d = new Division();
            assertNull(d.getId());
            assertNull(d.getName());
        }

        @Test
        void builder() {
            var d = Division.builder().id(1L).name("Builder").build();
            assertEquals(1L, d.getId());
            assertEquals("Builder", d.getName());
        }

        @Test
        void constructorWithNullDepartmentsList() {
            var d = new Division(1L, "Div", null);
            assertEquals(1L, d.getId());
            assertEquals("Div", d.getName());
            assertNull(d.getDepartments());
        }

        @Test
        void setNameAfterConstructionThenValidate() {
            var d = new Division();
            d.setName("Test Division");
            assertEquals("Test Division", d.getName());
            assertDoesNotThrow(d::validateData);
        }

        @Test
        void toStringVerification() {
            var d = Division.builder().id(1L).name("ToString").build();
            assertNotNull(d.toString());
        }

        @Test
        void constructorWithNullName() {
            var d = new Division(1L, null, null);
            assertNull(d.getName());
            assertThrows(InvalidDataException.class, d::validateData);
        }

        @Test
        void nameExceedsMaxLength() {
            var d = Division.builder().name("a".repeat(101)).build();
            assertDoesNotThrow(d::validateData);
        }

        @Test
        void noArgsConstructorThenSetFields() {
            var d = new Division();
            assertNull(d.getId());
            assertNull(d.getName());
            d.setId(5L);
            d.setName("Late Set");
            assertEquals(5L, d.getId());
            assertEquals("Late Set", d.getName());
            assertDoesNotThrow(d::validateData);
        }

        @Test
        void builderOnlyId() {
            var d = Division.builder().id(1L).build();
            assertNull(d.getName());
            assertEquals(1L, d.getId());
        }

        @Test
        void builderOnlyName() {
            var d = Division.builder().name("OnlyName").build();
            assertEquals("OnlyName", d.getName());
            assertNull(d.getId());
        }

        @Test
        void builderNeither() {
            var d = Division.builder().build();
            assertNull(d.getId());
            assertNull(d.getName());
        }

        @Test
        void constructorWithIdAndNameNullDepts() {
            var d = new Division(2L, "Two", null);
            assertEquals(2L, d.getId());
            assertEquals("Two", d.getName());
            assertNull(d.getDepartments());
        }

        @Test
        void constructorWithIdAndNameEmptyDepts() {
            var d = new Division(3L, "Three", List.of());
            assertEquals(3L, d.getId());
            assertEquals("Three", d.getName());
            assertNotNull(d.getDepartments());
        }

        @Test
        void setIdThenGet() {
            var d = new Division();
            d.setId(99L);
            assertEquals(99L, d.getId());
        }

        @Test
        void setNameThenGet() {
            var d = new Division();
            d.setName("NameSet");
            assertEquals("NameSet", d.getName());
        }

        @Test
        void setNullName() {
            var d = new Division();
            d.setName(null);
            assertNull(d.getName());
        }

        @Test
        void setEmptyName() {
            var d = new Division();
            d.setName("");
            assertEquals("", d.getName());
        }

        @Test
        void overwriteId() {
            var d = Division.builder().id(1L).build();
            d.setId(2L);
            assertEquals(2L, d.getId());
        }

        @Test
        void overwriteName() {
            var d = Division.builder().name("First").build();
            d.setName("Second");
            assertEquals("Second", d.getName());
        }

        @Test
        void idZero() {
            var d = Division.builder().id(0L).name("Zero").build();
            assertEquals(0L, d.getId());
            assertDoesNotThrow(d::validateData);
        }

        @Test
        void idMaxValue() {
            var d = Division.builder().id(Long.MAX_VALUE).name("Max").build();
            assertEquals(Long.MAX_VALUE, d.getId());
            assertDoesNotThrow(d::validateData);
        }

        @Test
        void unicodeJapaneseName() {
            var d = Division.builder().name("事業部").build();
            assertDoesNotThrow(d::validateData);
        }

        @Test
        void unicodeArabicName() {
            var d = Division.builder().name("قسم").build();
            assertDoesNotThrow(d::validateData);
        }

        @Test
        void unicodeEmojiName() {
            var d = Division.builder().name("🏢 Test").build();
            assertDoesNotThrow(d::validateData);
        }

        @Test
        void setDepartmentsThenGet() {
            var d = new Division();
            var depts = List.<Department>of();
            d.setDepartments(depts);
            assertEquals(depts, d.getDepartments());
        }

        @Test
        void constructorNoArgsThenSetDepartments() {
            var d = new Division();
            d.setId(10L);
            d.setName("DeptDiv");
            d.setDepartments(List.of());
            assertEquals(10L, d.getId());
            assertEquals("DeptDiv", d.getName());
            assertNotNull(d.getDepartments());
        }

        @Test
        void validateNameTrimmed() {
            var d = Division.builder().name("  Trimmed  ").build();
            assertDoesNotThrow(d::validateData);
        }

        @Test
        void constructorWithNullIdValidName() {
            var d = new Division(null, "NoId", null);
            assertNull(d.getId());
            assertEquals("NoId", d.getName());
        }

        @Test
        void constructorWithNameNull() {
            var d = new Division(5L, null, null);
            assertEquals(5L, d.getId());
            assertNull(d.getName());
            assertThrows(InvalidDataException.class, d::validateData);
        }

        @Test
        void equalsAndHashCode() {
            var d1 = Division.builder().id(1L).name("Same").build();
            var d2 = Division.builder().id(1L).name("Same").build();
            assertNotEquals(d1, d2);
        }

        @Test
        void toStringContainsName() {
            var d = Division.builder().name("ToStringTest").build();
            assertNotNull(d.toString());
        }

        @Test
        void setAfterValidate() {
            var d = Division.builder().name("Original").build();
            assertDoesNotThrow(d::validateData);
            d.setName("Changed");
            assertEquals("Changed", d.getName());
            assertDoesNotThrow(d::validateData);
        }

        @Test
        void builderWithNullName() {
            var d = Division.builder().name(null).build();
            assertNull(d.getName());
            assertThrows(InvalidDataException.class, d::validateData);
        }

        @Test
        void nameAndIdBoundary() {
            var d = Division.builder().id(0L).name("").build();
            assertEquals(0L, d.getId());
            assertEquals("", d.getName());
            assertThrows(InvalidDataException.class, d::validateData);
        }

        @Test
        void setAfterConstructorThenReSet() {
            var d = new Division();
            d.setName("A");
            d.setName("B");
            assertEquals("B", d.getName());
        }

        @Test
        void builderAllFields() {
            var d = Division.builder().id(5L).name("AllFields").departments(List.of()).build();
            assertEquals(5L, d.getId());
            assertEquals("AllFields", d.getName());
            assertNotNull(d.getDepartments());
        }

        @Test
        void setIdNull() {
            var d = Division.builder().id(5L).name("Test").build();
            d.setId(null);
            assertNull(d.getId());
        }

        @ParameterizedTest
        @ValueSource(strings = {"\u2000", "\u2001", "\u2002", "\u2003"})
        void invalidNameUnicodeWhitespace(String name) {
            var d = Division.builder().name(name).build();
            var ex = assertThrows(InvalidDataException.class, d::validateData);
            assertEquals("Division name is required", ex.getMessage());
        }

        @Test
        void nameExceedsMaxLengthByALot() {
            var d = Division.builder().name("a".repeat(500)).build();
            assertDoesNotThrow(d::validateData);
        }

        @Test
        void builderWithIdNullExplicit() {
            var d = Division.builder().id(null).name("NullId").build();
            assertNull(d.getId());
            assertEquals("NullId", d.getName());
        }

        @Test
        void constructorWithDepartmentsList() {
            var dept = Department.builder().id(1L).name("D").division(null).build();
            var d = new Division(1L, "Div", List.of(dept));
            assertEquals(1L, d.getId());
            assertEquals("Div", d.getName());
            assertEquals(1, d.getDepartments().size());
        }

        @ParameterizedTest
        @ValueSource(strings = {"a", "b", "Z"})
        void validSingleCharacterName(String name) {
            var d = Division.builder().name(name).build();
            assertDoesNotThrow(d::validateData);
        }

        @Test
        void nameLongerThanMax() {
            var d = Division.builder().name("a".repeat(200)).build();
            assertDoesNotThrow(d::validateData);
        }
    }

    @Nested
    class DepartmentValidation {
        private Division division() {
            return Division.builder().id(1L).name("D").build();
        }

        @Test
        void valid() {
            var d = Department.builder().name("Test Dept").division(division()).build();
            assertDoesNotThrow(d::validateData);
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {" ", "  ", "\t", "\n"})
        void invalidName(String name) {
            var d = Department.builder().name(name).division(division()).build();
            var ex = assertThrows(InvalidDataException.class, d::validateData);
            assertEquals("Department name is required", ex.getMessage());
        }

        @Test
        void nullDivision() {
            var d = Department.builder().name("Dept").division(null).build();
            var ex = assertThrows(InvalidDataException.class, d::validateData);
            assertEquals("Division is required", ex.getMessage());
        }

        @Test
        void nameWithSpecialChars() {
            var d = Department.builder().name("Dept #2 (IT)").division(division()).build();
            assertDoesNotThrow(d::validateData);
        }

        @Test
        void allArgsConstructor() {
            var div = division();
            var d = new Department(1L, "Dept", div, null);
            assertEquals(1L, d.getId());
            assertEquals("Dept", d.getName());
            assertEquals(div, d.getDivision());
        }

        @Test
        void noArgsConstructor() {
            var d = new Department();
            assertNull(d.getId());
            assertNull(d.getName());
            assertNull(d.getDivision());
        }

        @Test
        void builderWithAllFields() {
            var div = division();
            var d = Department.builder().id(1L).name("Builder").division(div).build();
            assertEquals(1L, d.getId());
            assertEquals("Builder", d.getName());
            assertEquals(div, d.getDivision());
        }

        @Test
        void settersAndGetters() {
            var div = division();
            var d = new Department();
            d.setId(2L);
            d.setName("Setters");
            d.setDivision(div);
            assertEquals(2L, d.getId());
            assertEquals("Setters", d.getName());
            assertEquals(div, d.getDivision());
        }

        @Test
        void nameMaxLength() {
            var d = Department.builder().name("a".repeat(100)).division(division()).build();
            assertDoesNotThrow(d::validateData);
        }

        @Test
        void validateAfterSettingNullDivision() {
            var d = Department.builder().name("Dept").division(division()).build();
            assertDoesNotThrow(d::validateData);
            d.setDivision(null);
            var ex = assertThrows(InvalidDataException.class, d::validateData);
            assertEquals("Division is required", ex.getMessage());
        }

        @Test
        void constructorWithNullIdAndName() {
            var d = new Department(null, null, division(), null);
            assertNull(d.getId());
            assertNull(d.getName());
            assertThrows(InvalidDataException.class, d::validateData);
        }

        @Test
        void builderOnlyId() {
            var d = Department.builder().id(1L).build();
            assertEquals(1L, d.getId());
            assertNull(d.getName());
            assertNull(d.getDivision());
        }

        @Test
        void builderOnlyName() {
            var d = Department.builder().name("OnlyName").build();
            assertEquals("OnlyName", d.getName());
            assertNull(d.getId());
            assertNull(d.getDivision());
        }

        @Test
        void builderOnlyDivision() {
            var d = Department.builder().division(division()).build();
            assertNotNull(d.getDivision());
            assertNull(d.getId());
            assertNull(d.getName());
        }

        @Test
        void builderNameAndDivision() {
            var div = division();
            var d = Department.builder().name("Dept").division(div).build();
            assertEquals("Dept", d.getName());
            assertEquals(div, d.getDivision());
            assertNull(d.getId());
        }

        @Test
        void builderIdAndDivision() {
            var div = division();
            var d = Department.builder().id(1L).division(div).build();
            assertEquals(1L, d.getId());
            assertEquals(div, d.getDivision());
            assertNull(d.getName());
        }

        @Test
        void builderNeither() {
            var d = Department.builder().build();
            assertNull(d.getId());
            assertNull(d.getName());
            assertNull(d.getDivision());
        }

        @Test
        void constructorWithoutDivision() {
            var d = new Department(1L, "Dept", null, null);
            assertEquals(1L, d.getId());
            assertEquals("Dept", d.getName());
            assertNull(d.getDivision());
            assertThrows(InvalidDataException.class, d::validateData);
        }

        @Test
        void constructorWithDivisionAndNullProjects() {
            var div = division();
            var d = new Department(1L, "Dept", div, null);
            assertEquals(1L, d.getId());
            assertEquals("Dept", d.getName());
            assertEquals(div, d.getDivision());
            assertNull(d.getProjects());
        }

        @Test
        void constructorWithDivisionAndEmptyProjects() {
            var div = division();
            var d = new Department(1L, "Dept", div, List.of());
            assertEquals(1L, d.getId());
            assertEquals("Dept", d.getName());
            assertEquals(div, d.getDivision());
            assertNotNull(d.getProjects());
        }

        @Test
        void noArgsConstructorThenSetters() {
            var div = division();
            var d = new Department();
            d.setId(3L);
            d.setName("Late");
            d.setDivision(div);
            assertEquals(3L, d.getId());
            assertEquals("Late", d.getName());
            assertEquals(div, d.getDivision());
        }

        @Test
        void setNameNull() {
            var d = Department.builder().name("Dept").division(division()).build();
            d.setName(null);
            assertNull(d.getName());
            assertThrows(InvalidDataException.class, d::validateData);
        }

        @Test
        void setNameEmpty() {
            var d = Department.builder().name("Dept").division(division()).build();
            d.setName("");
            assertEquals("", d.getName());
            assertThrows(InvalidDataException.class, d::validateData);
        }

        @Test
        void setDivisionToDifferentDivision() {
            var div1 = Division.builder().id(1L).name("Div1").build();
            var div2 = Division.builder().id(2L).name("Div2").build();
            var d = Department.builder().name("Dept").division(div1).build();
            assertEquals(div1, d.getDivision());
            d.setDivision(div2);
            assertEquals(div2, d.getDivision());
        }

        @Test
        void overwriteName() {
            var d = Department.builder().name("First").division(division()).build();
            d.setName("Second");
            assertEquals("Second", d.getName());
        }

        @Test
        void overwriteId() {
            var d = Department.builder().id(1L).name("Dept").division(division()).build();
            d.setId(2L);
            assertEquals(2L, d.getId());
        }

        @Test
        void idZero() {
            var d = Department.builder().id(0L).name("Zero").division(division()).build();
            assertEquals(0L, d.getId());
            assertDoesNotThrow(d::validateData);
        }

        @Test
        void idMaxValue() {
            var d = Department.builder().id(Long.MAX_VALUE).name("Max").division(division()).build();
            assertEquals(Long.MAX_VALUE, d.getId());
            assertDoesNotThrow(d::validateData);
        }

        @Test
        void unicodeName() {
            var d = Department.builder().name("部署").division(division()).build();
            assertDoesNotThrow(d::validateData);
        }

        @Test
        void setProjectsThenGet() {
            var d = Department.builder().name("Dept").division(division()).build();
            var projects = List.<Project>of();
            d.setProjects(projects);
            assertEquals(projects, d.getProjects());
        }

        @Test
        void setDivisionAfterValidate() {
            var d = Department.builder().name("Dept").division(division()).build();
            assertDoesNotThrow(d::validateData);
            var div2 = Division.builder().id(2L).name("Div2").build();
            d.setDivision(div2);
            assertEquals(div2, d.getDivision());
            assertDoesNotThrow(d::validateData);
        }

        @Test
        void equalsAndHashCode() {
            var div = division();
            var d1 = Department.builder().id(1L).name("Dept").division(div).build();
            var d2 = Department.builder().id(1L).name("Dept").division(div).build();
            assertNotEquals(d1, d2);
        }

        @Test
        void toStringNotNull() {
            var d = Department.builder().name("Dept").division(division()).build();
            assertNotNull(d.toString());
        }

        @Test
        void nameExceedsMaxLength() {
            var d = Department.builder().name("a".repeat(200)).division(division()).build();
            assertDoesNotThrow(d::validateData);
        }

        @Test
        void constructorWithNullDivision() {
            var d = new Department(1L, "Dept", null, null);
            assertNull(d.getDivision());
            assertThrows(InvalidDataException.class, d::validateData);
        }

        @Test
        void noArgsConstructorThenValidate() {
            var d = new Department();
            assertThrows(InvalidDataException.class, d::validateData);
        }

        @Test
        void setDivisionNullAfterConstruction() {
            var d = new Department(1L, "Dept", division(), null);
            assertDoesNotThrow(d::validateData);
            d.setDivision(null);
            assertThrows(InvalidDataException.class, d::validateData);
        }

        @Test
        void setIdNull() {
            var d = Department.builder().id(1L).name("Dept").division(division()).build();
            d.setId(null);
            assertNull(d.getId());
        }

        @Test
        void setNameThenValidateWithNullDivision() {
            var d = new Department();
            d.setName("Dept");
            assertThrows(InvalidDataException.class, d::validateData);
        }

        @Test
        void setDivisionThenValidateWithNullName() {
            var d = new Department();
            d.setDivision(division());
            assertThrows(InvalidDataException.class, d::validateData);
        }

        @Test
        void setAllFieldsThenValidate() {
            var d = new Department();
            d.setId(5L);
            d.setName("Complete");
            d.setDivision(division());
            d.setProjects(List.of());
            assertDoesNotThrow(d::validateData);
        }

        @Test
        void divisionWithMinimalDivision() {
            var div = Division.builder().id(1L).name("D").build();
            var d = Department.builder().name("Dept").division(div).build();
            assertDoesNotThrow(d::validateData);
        }

        @Test
        void nameAtBoundaryHundred() {
            var d = Department.builder().name("a".repeat(100)).division(division()).build();
            assertEquals(100, d.getName().length());
            assertDoesNotThrow(d::validateData);
        }

        @Test
        void divisionWithoutId() {
            var div = Division.builder().name("NoId").build();
            var d = Department.builder().name("Dept").division(div).build();
            assertNull(d.getDivision().getId());
            assertDoesNotThrow(d::validateData);
        }

        @Test
        void setAfterConstructorThenReSetName() {
            var d = new Department();
            d.setName("A");
            d.setName("B");
            assertEquals("B", d.getName());
        }

        @Test
        void constructorWithNullId() {
            var d = new Department(null, "Dept", division(), null);
            assertNull(d.getId());
            assertEquals("Dept", d.getName());
            assertDoesNotThrow(d::validateData);
        }

        @ParameterizedTest
        @ValueSource(strings = {"\u2002", "\u2003"})
        void invalidNameUnicodeWhitespace(String name) {
            var d = Department.builder().name(name).division(division()).build();
            var ex = assertThrows(InvalidDataException.class, d::validateData);
            assertEquals("Department name is required", ex.getMessage());
        }

        @Test
        void nameWithSpecialCharactersOnly() {
            var d = Department.builder().name("!@#$%").division(division()).build();
            assertDoesNotThrow(d::validateData);
        }

        @Test
        void setProjectsNull() {
            var d = Department.builder().name("Dept").division(division()).build();
            d.setProjects(null);
            assertNull(d.getProjects());
        }

        @Test
        void nameAtBoundaryNinetyNine() {
            var d = Department.builder().name("a".repeat(99)).division(division()).build();
            assertDoesNotThrow(d::validateData);
        }
    }

    @Nested
    class ProjectValidation {
        private Department department() {
            return Department.builder().id(1L).name("D").division(Division.builder().id(1L).name("Div").build()).build();
        }

        @Test
        void valid() {
            var p = Project.builder().name("Test Project").department(department()).build();
            assertDoesNotThrow(p::validateData);
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {" ", "  ", "\t", "\n"})
        void invalidName(String name) {
            var p = Project.builder().name(name).department(department()).build();
            var ex = assertThrows(InvalidDataException.class, p::validateData);
            assertEquals("Project name is required", ex.getMessage());
        }

        @Test
        void nullDepartment() {
            var p = Project.builder().name("Proj").department(null).build();
            var ex = assertThrows(InvalidDataException.class, p::validateData);
            assertEquals("Department is required", ex.getMessage());
        }

        @Test
        void allArgsConstructor() {
            var dept = department();
            var p = new Project(1L, "Proj", dept, null);
            assertEquals(1L, p.getId());
            assertEquals("Proj", p.getName());
            assertEquals(dept, p.getDepartment());
        }

        @Test
        void noArgsConstructor() {
            var p = new Project();
            assertNull(p.getId());
            assertNull(p.getName());
            assertNull(p.getDepartment());
        }

        @Test
        void builderWithAllFields() {
            var dept = department();
            var p = Project.builder().id(1L).name("Builder").department(dept).build();
            assertEquals(1L, p.getId());
            assertEquals("Builder", p.getName());
            assertEquals(dept, p.getDepartment());
        }

        @Test
        void settersAndGetters() {
            var dept = department();
            var p = new Project();
            p.setId(2L);
            p.setName("Setters");
            p.setDepartment(dept);
            assertEquals(2L, p.getId());
            assertEquals("Setters", p.getName());
            assertEquals(dept, p.getDepartment());
        }

        @Test
        void setNullDepartmentAfterConstructionThenValidate() {
            var dept = department();
            var p = Project.builder().name("Proj").department(dept).build();
            assertDoesNotThrow(p::validateData);
            p.setDepartment(null);
            var ex = assertThrows(InvalidDataException.class, p::validateData);
            assertEquals("Department is required", ex.getMessage());
        }

        @Test
        void nameMaxLength() {
            var p = Project.builder().name("a".repeat(100)).department(department()).build();
            assertDoesNotThrow(p::validateData);
        }

        @Test
        void nameSingleCharacter() {
            var p = Project.builder().name("a").department(department()).build();
            assertDoesNotThrow(p::validateData);
        }

        @Test
        void departmentHierarchyTraversal() {
            var div = Division.builder().id(1L).name("Div").build();
            var dept = Department.builder().id(1L).name("Dept").division(div).build();
            var proj = Project.builder().id(1L).name("Proj").department(dept).build();
            assertDoesNotThrow(proj::validateData);
            assertEquals(div, proj.getDepartment().getDivision());
            assertEquals("Div", proj.getDepartment().getDivision().getName());
        }

        @Test
        void constructorWithNullIdAndName() {
            var p = new Project(null, null, department(), null);
            assertNull(p.getId());
            assertNull(p.getName());
            assertThrows(InvalidDataException.class, p::validateData);
        }

        @Test
        void builderOnlyId() {
            var p = Project.builder().id(1L).build();
            assertEquals(1L, p.getId());
            assertNull(p.getName());
            assertNull(p.getDepartment());
        }

        @Test
        void builderOnlyName() {
            var p = Project.builder().name("OnlyName").build();
            assertEquals("OnlyName", p.getName());
            assertNull(p.getId());
            assertNull(p.getDepartment());
        }

        @Test
        void builderOnlyDepartment() {
            var p = Project.builder().department(department()).build();
            assertNotNull(p.getDepartment());
            assertNull(p.getId());
            assertNull(p.getName());
        }

        @Test
        void builderNameAndDepartment() {
            var dept = department();
            var p = Project.builder().name("Proj").department(dept).build();
            assertEquals("Proj", p.getName());
            assertEquals(dept, p.getDepartment());
            assertNull(p.getId());
        }

        @Test
        void builderIdAndDepartment() {
            var dept = department();
            var p = Project.builder().id(1L).department(dept).build();
            assertEquals(1L, p.getId());
            assertEquals(dept, p.getDepartment());
            assertNull(p.getName());
        }

        @Test
        void builderNeither() {
            var p = Project.builder().build();
            assertNull(p.getId());
            assertNull(p.getName());
            assertNull(p.getDepartment());
        }

        @Test
        void constructorWithoutDepartment() {
            var p = new Project(1L, "Proj", null, null);
            assertEquals(1L, p.getId());
            assertEquals("Proj", p.getName());
            assertNull(p.getDepartment());
            assertThrows(InvalidDataException.class, p::validateData);
        }

        @Test
        void constructorWithDepartmentAndNullProducts() {
            var dept = department();
            var p = new Project(1L, "Proj", dept, null);
            assertEquals(1L, p.getId());
            assertEquals("Proj", p.getName());
            assertEquals(dept, p.getDepartment());
            assertNull(p.getProducts());
        }

        @Test
        void constructorWithDepartmentAndEmptyProducts() {
            var dept = department();
            var p = new Project(1L, "Proj", dept, List.of());
            assertEquals(1L, p.getId());
            assertEquals("Proj", p.getName());
            assertEquals(dept, p.getDepartment());
            assertNotNull(p.getProducts());
        }

        @Test
        void noArgsConstructorThenSetters() {
            var dept = department();
            var p = new Project();
            p.setId(3L);
            p.setName("Late");
            p.setDepartment(dept);
            assertEquals(3L, p.getId());
            assertEquals("Late", p.getName());
            assertEquals(dept, p.getDepartment());
        }

        @Test
        void setNameNull() {
            var p = Project.builder().name("Proj").department(department()).build();
            p.setName(null);
            assertNull(p.getName());
            assertThrows(InvalidDataException.class, p::validateData);
        }

        @Test
        void setNameEmpty() {
            var p = Project.builder().name("Proj").department(department()).build();
            p.setName("");
            assertEquals("", p.getName());
            assertThrows(InvalidDataException.class, p::validateData);
        }

        @Test
        void setDepartmentToDifferentDepartment() {
            var dept1 = department();
            var dept2 = Department.builder().id(2L).name("Dept2").division(Division.builder().id(1L).name("Div").build()).build();
            var p = Project.builder().name("Proj").department(dept1).build();
            assertEquals(dept1, p.getDepartment());
            p.setDepartment(dept2);
            assertEquals(dept2, p.getDepartment());
        }

        @Test
        void overwriteName() {
            var p = Project.builder().name("First").department(department()).build();
            p.setName("Second");
            assertEquals("Second", p.getName());
        }

        @Test
        void overwriteId() {
            var p = Project.builder().id(1L).name("Proj").department(department()).build();
            p.setId(2L);
            assertEquals(2L, p.getId());
        }

        @Test
        void idZero() {
            var p = Project.builder().id(0L).name("Zero").department(department()).build();
            assertEquals(0L, p.getId());
            assertDoesNotThrow(p::validateData);
        }

        @Test
        void idMaxValue() {
            var p = Project.builder().id(Long.MAX_VALUE).name("Max").department(department()).build();
            assertEquals(Long.MAX_VALUE, p.getId());
            assertDoesNotThrow(p::validateData);
        }

        @Test
        void unicodeName() {
            var p = Project.builder().name("プロジェクト").department(department()).build();
            assertDoesNotThrow(p::validateData);
        }

        @Test
        void setProductsThenGet() {
            var p = Project.builder().name("Proj").department(department()).build();
            var products = List.<Product>of();
            p.setProducts(products);
            assertEquals(products, p.getProducts());
        }

        @Test
        void equalsAndHashCode() {
            var dept = department();
            var p1 = Project.builder().id(1L).name("Proj").department(dept).build();
            var p2 = Project.builder().id(1L).name("Proj").department(dept).build();
            assertNotEquals(p1, p2);
        }

        @Test
        void toStringNotNull() {
            var p = Project.builder().name("Proj").department(department()).build();
            assertNotNull(p.toString());
        }

        @Test
        void nameExceedsMaxLength() {
            var p = Project.builder().name("a".repeat(200)).department(department()).build();
            assertDoesNotThrow(p::validateData);
        }

        @Test
        void constructorWithNullDepartment() {
            var p = new Project(1L, "Proj", null, null);
            assertNull(p.getDepartment());
            assertThrows(InvalidDataException.class, p::validateData);
        }

        @Test
        void noArgsConstructorThenValidate() {
            var p = new Project();
            assertThrows(InvalidDataException.class, p::validateData);
        }

        @Test
        void setDepartmentNullAfterConstruction() {
            var p = new Project(1L, "Proj", department(), null);
            assertDoesNotThrow(p::validateData);
            p.setDepartment(null);
            assertThrows(InvalidDataException.class, p::validateData);
        }

        @Test
        void setIdNull() {
            var p = Project.builder().id(1L).name("Proj").department(department()).build();
            p.setId(null);
            assertNull(p.getId());
        }

        @Test
        void setNameThenValidateWithNullDepartment() {
            var p = new Project();
            p.setName("Proj");
            assertThrows(InvalidDataException.class, p::validateData);
        }

        @Test
        void setDepartmentThenValidateWithNullName() {
            var p = new Project();
            p.setDepartment(department());
            assertThrows(InvalidDataException.class, p::validateData);
        }

        @Test
        void setAllFieldsThenValidate() {
            var p = new Project();
            p.setId(5L);
            p.setName("Complete");
            p.setDepartment(department());
            p.setProducts(List.of());
            assertDoesNotThrow(p::validateData);
        }

        @Test
        void nameAtBoundaryHundred() {
            var p = Project.builder().name("a".repeat(100)).department(department()).build();
            assertEquals(100, p.getName().length());
            assertDoesNotThrow(p::validateData);
        }

        @Test
        void setAfterConstructorThenReSetName() {
            var p = new Project();
            p.setName("A");
            p.setName("B");
            assertEquals("B", p.getName());
        }

        @Test
        void constructorWithNullId() {
            var p = new Project(null, "Proj", department(), null);
            assertNull(p.getId());
            assertEquals("Proj", p.getName());
            assertDoesNotThrow(p::validateData);
        }

        @ParameterizedTest
        @ValueSource(strings = {"a", "Z", "1"})
        void validSingleCharacterName(String name) {
            var p = Project.builder().name(name).department(department()).build();
            assertDoesNotThrow(p::validateData);
        }

        @Test
        void departmentHierarchyWithDivision() {
            var div = Division.builder().id(1L).name("Div").build();
            var dept = Department.builder().id(1L).name("Dept").division(div).build();
            var proj = Project.builder().name("Proj").department(dept).build();
            assertDoesNotThrow(proj::validateData);
            assertEquals(div, proj.getDepartment().getDivision());
        }

        @Test
        void builderAllFields() {
            var dept = department();
            var p = Project.builder().id(10L).name("AllFields").department(dept).products(List.of()).build();
            assertEquals(10L, p.getId());
            assertEquals("AllFields", p.getName());
            assertEquals(dept, p.getDepartment());
            assertNotNull(p.getProducts());
        }

        @Test
        void nameWithSpecialCharacters() {
            var p = Project.builder().name("Project #42 (Special)!").department(department()).build();
            assertDoesNotThrow(p::validateData);
        }

        @Test
        void setProductsNull() {
            var p = Project.builder().name("Proj").department(department()).build();
            p.setProducts(null);
            assertNull(p.getProducts());
        }

        @Test
        void nameWithUnicodeBoundary() {
            var p = Project.builder().name("a".repeat(100)).department(department()).build();
            assertEquals(100, p.getName().length());
            assertDoesNotThrow(p::validateData);
        }
    }

    @Nested
    class ProductValidation {
        private Project project() {
            return Project.builder().id(1L).name("P").department(
                Department.builder().id(1L).name("D").division(
                    Division.builder().id(1L).name("Div").build()).build()).build();
        }

        @Test
        void valid() {
            var p = Product.builder().name("Test Product").version("v01.02.003").project(project()).build();
            assertDoesNotThrow(p::validateData);
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {" ", "  ", "\t", "\n"})
        void invalidName(String name) {
            var p = Product.builder().name(name).version("v01.02.003").project(project()).build();
            var ex = assertThrows(InvalidDataException.class, p::validateData);
            assertEquals("Product name is required", ex.getMessage());
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {" ", "v1.2.3", "01.02.003", "v1.02.003", "v01.2.003", "v01.02.3", "v01.02.0000", "abc", ""})
        void invalidVersion(String version) {
            var p = Product.builder().name("Prod").version(version).project(project()).build();
            var ex = assertThrows(InvalidDataException.class, p::validateData);
            assertTrue(ex.getMessage().toLowerCase().contains("version"));
        }

        @Test
        void validVersionBoundaries() {
            assertDoesNotThrow(() -> Product.builder().name("P").version("v00.00.000").project(project()).build().validateData());
            assertDoesNotThrow(() -> Product.builder().name("P").version("v99.99.999").project(project()).build().validateData());
            assertDoesNotThrow(() -> Product.builder().name("P").version("v50.50.500").project(project()).build().validateData());
        }

        @Test
        void nullProject() {
            var p = Product.builder().name("Prod").version("v01.02.003").project(null).build();
            var ex = assertThrows(InvalidDataException.class, p::validateData);
            assertEquals("Project is required", ex.getMessage());
        }

        @Test
        void optionalFields() {
            var p = Product.builder().name("Prod").version("v01.02.003")
                .publishDate(LocalDateTime.of(2025, 6, 15, 10, 30))
                .description("Some description")
                .project(project()).build();
            assertDoesNotThrow(p::validateData);
            assertEquals(LocalDateTime.of(2025, 6, 15, 10, 30), p.getPublishDate());
            assertEquals("Some description", p.getDescription());
        }

        @Test
        void nullOptionalFields() {
            var p = Product.builder().name("Prod").version("v01.02.003")
                .publishDate(null).description(null)
                .project(project()).build();
            assertDoesNotThrow(p::validateData);
            assertNull(p.getPublishDate());
            assertNull(p.getDescription());
        }

        @Test
        void descriptionMaxLength() {
            var p = Product.builder().name("Prod").version("v01.02.003")
                .description("a".repeat(500)).project(project()).build();
            assertDoesNotThrow(p::validateData);
        }

        @Test
        void allArgsConstructorWithOptionals() {
            var proj = project();
            var date = LocalDateTime.of(2025, 6, 15, 10, 30);
            var p = new Product(1L, "Prod", "v01.02.003", date, "Desc", proj);
            assertEquals(1L, p.getId());
            assertEquals("Prod", p.getName());
            assertEquals("v01.02.003", p.getVersion());
            assertEquals(date, p.getPublishDate());
            assertEquals("Desc", p.getDescription());
            assertEquals(proj, p.getProject());
        }

        @Test
        void noArgsConstructor() {
            var p = new Product();
            assertNull(p.getId());
            assertNull(p.getName());
            assertNull(p.getVersion());
            assertNull(p.getPublishDate());
            assertNull(p.getDescription());
            assertNull(p.getProject());
        }

        @Test
        void builderWithAllFields() {
            var proj = project();
            var date = LocalDateTime.of(2025, 7, 4, 12, 0);
            var p = Product.builder().id(1L).name("Builder").version("v01.02.003")
                .publishDate(date).description("Builder desc").project(proj).build();
            assertEquals(1L, p.getId());
            assertEquals("Builder", p.getName());
            assertEquals("v01.02.003", p.getVersion());
            assertEquals(date, p.getPublishDate());
            assertEquals("Builder desc", p.getDescription());
            assertEquals(proj, p.getProject());
        }

        @Test
        void settersAndGetters() {
            var proj = project();
            var date = LocalDateTime.of(2025, 8, 1, 9, 15);
            var p = new Product();
            p.setId(2L);
            p.setName("Setter");
            p.setVersion("v99.99.999");
            p.setPublishDate(date);
            p.setDescription("Setter desc");
            p.setProject(proj);
            assertEquals(2L, p.getId());
            assertEquals("Setter", p.getName());
            assertEquals("v99.99.999", p.getVersion());
            assertEquals(date, p.getPublishDate());
            assertEquals("Setter desc", p.getDescription());
            assertEquals(proj, p.getProject());
        }

        @Test
        void setNullProjectAfterConstructionThenValidate() {
            var proj = project();
            var p = Product.builder().name("Prod").version("v01.02.003").project(proj).build();
            assertDoesNotThrow(p::validateData);
            p.setProject(null);
            var ex = assertThrows(InvalidDataException.class, p::validateData);
            assertEquals("Project is required", ex.getMessage());
        }

        @Test
        void setNullVersionThenValidate() {
            var p = Product.builder().name("Prod").version("v01.02.003").project(project()).build();
            assertDoesNotThrow(p::validateData);
            p.setVersion(null);
            var ex = assertThrows(InvalidDataException.class, p::validateData);
            assertTrue(ex.getMessage().toLowerCase().contains("version"));
        }

        @Test
        void setPublishDateRoundTrip() {
            var p = Product.builder().name("Prod").version("v01.02.003").project(project()).build();
            assertNull(p.getPublishDate());
            var date = LocalDateTime.of(2026, 1, 1, 0, 0);
            p.setPublishDate(date);
            assertEquals(date, p.getPublishDate());
            p.setPublishDate(null);
            assertNull(p.getPublishDate());
        }

        @Test
        void setDescriptionRoundTrip() {
            var p = Product.builder().name("Prod").version("v01.02.003").project(project()).build();
            assertNull(p.getDescription());
            p.setDescription("Round trip");
            assertEquals("Round trip", p.getDescription());
            p.setDescription(null);
            assertNull(p.getDescription());
        }

        @Test
        void nameMaxLength() {
            var p = Product.builder().name("a".repeat(100)).version("v01.02.003").project(project()).build();
            assertDoesNotThrow(p::validateData);
        }

        @Test
        void builderOnlyId() {
            var p = Product.builder().id(1L).build();
            assertEquals(1L, p.getId());
            assertNull(p.getName());
            assertNull(p.getVersion());
            assertNull(p.getProject());
        }

        @Test
        void builderOnlyName() {
            var p = Product.builder().name("OnlyName").build();
            assertEquals("OnlyName", p.getName());
            assertNull(p.getId());
            assertNull(p.getVersion());
            assertNull(p.getProject());
        }

        @Test
        void builderOnlyVersion() {
            var p = Product.builder().version("v01.02.003").build();
            assertEquals("v01.02.003", p.getVersion());
            assertNull(p.getId());
            assertNull(p.getName());
            assertNull(p.getProject());
        }

        @Test
        void builderNameAndVersionAndProject() {
            var proj = project();
            var p = Product.builder().name("Prod").version("v01.02.003").project(proj).build();
            assertEquals("Prod", p.getName());
            assertEquals("v01.02.003", p.getVersion());
            assertEquals(proj, p.getProject());
            assertNull(p.getId());
        }

        @Test
        void builderWithDescriptionOnly() {
            var p = Product.builder().description("Desc only").build();
            assertEquals("Desc only", p.getDescription());
            assertNull(p.getName());
            assertNull(p.getVersion());
        }

        @Test
        void builderWithPublishDateOnly() {
            var date = LocalDateTime.now();
            var p = Product.builder().publishDate(date).build();
            assertEquals(date, p.getPublishDate());
        }

        @Test
        void constructorWithOptionalsNull() {
            var proj = project();
            var p = new Product(1L, "Prod", "v01.02.003", null, null, proj);
            assertEquals(1L, p.getId());
            assertEquals("Prod", p.getName());
            assertEquals("v01.02.003", p.getVersion());
            assertNull(p.getPublishDate());
            assertNull(p.getDescription());
            assertEquals(proj, p.getProject());
        }

        @Test
        void constructorWithAllNulls() {
            var p = new Product(null, null, null, null, null, null);
            assertNull(p.getId());
            assertNull(p.getName());
            assertNull(p.getVersion());
            assertNull(p.getPublishDate());
            assertNull(p.getDescription());
            assertNull(p.getProject());
            assertThrows(InvalidDataException.class, p::validateData);
        }

        @Test
        void noArgsConstructorThenSetters() {
            var proj = project();
            var date = LocalDateTime.of(2026, 1, 1, 0, 0);
            var p = new Product();
            p.setId(3L);
            p.setName("Late");
            p.setVersion("v99.99.999");
            p.setPublishDate(date);
            p.setDescription("Late desc");
            p.setProject(proj);
            assertEquals(3L, p.getId());
            assertEquals("Late", p.getName());
            assertEquals("v99.99.999", p.getVersion());
            assertEquals(date, p.getPublishDate());
            assertEquals("Late desc", p.getDescription());
            assertEquals(proj, p.getProject());
        }

        @Test
        void setNameNull() {
            var p = Product.builder().name("Prod").version("v01.02.003").project(project()).build();
            p.setName(null);
            assertNull(p.getName());
            assertThrows(InvalidDataException.class, p::validateData);
        }

        @Test
        void setNameEmpty() {
            var p = Product.builder().name("Prod").version("v01.02.003").project(project()).build();
            p.setName("");
            assertEquals("", p.getName());
            assertThrows(InvalidDataException.class, p::validateData);
        }

        @Test
        void setVersionEmpty() {
            var p = Product.builder().name("Prod").version("v01.02.003").project(project()).build();
            p.setVersion("");
            assertEquals("", p.getVersion());
            assertThrows(InvalidDataException.class, p::validateData);
        }

        @Test
        void setProjectToDifferentProject() {
            var proj1 = project();
            var proj2 = Project.builder().id(2L).name("P2").department(
                Department.builder().id(1L).name("D").division(
                    Division.builder().id(1L).name("Div").build()).build()).build();
            var p = Product.builder().name("Prod").version("v01.02.003").project(proj1).build();
            assertEquals(proj1, p.getProject());
            p.setProject(proj2);
            assertEquals(proj2, p.getProject());
        }

        @Test
        void overwriteName() {
            var p = Product.builder().name("First").version("v01.02.003").project(project()).build();
            p.setName("Second");
            assertEquals("Second", p.getName());
        }

        @Test
        void overwriteVersion() {
            var p = Product.builder().name("Prod").version("v01.02.003").project(project()).build();
            p.setVersion("v99.99.999");
            assertEquals("v99.99.999", p.getVersion());
        }

        @Test
        void overwriteId() {
            var p = Product.builder().id(1L).name("Prod").version("v01.02.003").project(project()).build();
            p.setId(2L);
            assertEquals(2L, p.getId());
        }

        @Test
        void idZero() {
            var p = Product.builder().id(0L).name("Zero").version("v01.02.003").project(project()).build();
            assertEquals(0L, p.getId());
            assertDoesNotThrow(p::validateData);
        }

        @Test
        void idMaxValue() {
            var p = Product.builder().id(Long.MAX_VALUE).name("Max").version("v01.02.003").project(project()).build();
            assertEquals(Long.MAX_VALUE, p.getId());
            assertDoesNotThrow(p::validateData);
        }

        @Test
        void equalsAndHashCode() {
            var proj = project();
            var p1 = Product.builder().id(1L).name("Prod").version("v01.02.003").project(proj).build();
            var p2 = Product.builder().id(1L).name("Prod").version("v01.02.003").project(proj).build();
            assertNotEquals(p1, p2);
        }

        @Test
        void toStringNotNull() {
            var p = Product.builder().name("Prod").version("v01.02.003").project(project()).build();
            assertNotNull(p.toString());
        }

        @Test
        void nameExceedsMaxLength() {
            var p = Product.builder().name("a".repeat(200)).version("v01.02.003").project(project()).build();
            assertDoesNotThrow(p::validateData);
        }

        @Test
        void constructorWithInvalidVersion() {
            var p = new Product(1L, "Prod", "bad", null, null, project());
            assertEquals("bad", p.getVersion());
            assertThrows(InvalidDataException.class, p::validateData);
        }

        @Test
        void noArgsConstructorThenValidate() {
            var p = new Product();
            assertThrows(InvalidDataException.class, p::validateData);
        }

        @Test
        void setIdNull() {
            var p = Product.builder().id(1L).name("Prod").version("v01.02.003").project(project()).build();
            p.setId(null);
            assertNull(p.getId());
        }

        @Test
        void setDescriptionEmpty() {
            var p = Product.builder().name("Prod").version("v01.02.003").project(project()).build();
            p.setDescription("");
            assertEquals("", p.getDescription());
            assertDoesNotThrow(p::validateData);
        }

        @Test
        void setPublishDatePast() {
            var p = Product.builder().name("Prod").version("v01.02.003").project(project()).build();
            var past = LocalDateTime.of(2000, 1, 1, 0, 0);
            p.setPublishDate(past);
            assertEquals(past, p.getPublishDate());
            assertDoesNotThrow(p::validateData);
        }

        @Test
        void setPublishDateFuture() {
            var p = Product.builder().name("Prod").version("v01.02.003").project(project()).build();
            var future = LocalDateTime.of(2099, 12, 31, 23, 59);
            p.setPublishDate(future);
            assertEquals(future, p.getPublishDate());
            assertDoesNotThrow(p::validateData);
        }

        @Test
        void setPublishDateNow() {
            var p = Product.builder().name("Prod").version("v01.02.003").project(project()).build();
            var now = LocalDateTime.now();
            p.setPublishDate(now);
            assertEquals(now, p.getPublishDate());
            assertDoesNotThrow(p::validateData);
        }

        @Test
        void descriptionExceedsMaxLength() {
            var p = Product.builder().name("Prod").version("v01.02.003")
                .description("a".repeat(501)).project(project()).build();
            assertEquals(501, p.getDescription().length());
            assertDoesNotThrow(p::validateData);
        }

        @ParameterizedTest
        @ValueSource(strings = {"v1.2.3", "01.02.003", "v1.02.003", "v01.2.003", "v01.02.3", "v01.02.0000", "abc", "v", "v1", "v01.02", "v01.02.03", "v01.02. 003", "v01.02.03a", "V01.02.003", "v01.02.0034", "v01.02.00"})
        void manyInvalidVersions(String version) {
            var p = Product.builder().name("Prod").version(version).project(project()).build();
            var ex = assertThrows(InvalidDataException.class, p::validateData);
            assertTrue(ex.getMessage().toLowerCase().contains("version"));
        }

        @Test
        void builderAllFields() {
            var proj = project();
            var date = LocalDateTime.of(2026, 6, 15, 10, 30);
            var p = Product.builder().id(10L).name("AllFields").version("v99.99.999")
                .publishDate(date).description("All desc").project(proj).build();
            assertEquals(10L, p.getId());
            assertEquals("AllFields", p.getName());
            assertEquals("v99.99.999", p.getVersion());
            assertEquals(date, p.getPublishDate());
            assertEquals("All desc", p.getDescription());
            assertEquals(proj, p.getProject());
        }

        @Test
        void setInvalidVersionThenValid() {
            var p = Product.builder().name("Prod").version("v01.02.003").project(project()).build();
            p.setVersion("bad");
            assertThrows(InvalidDataException.class, p::validateData);
            p.setVersion("v99.99.999");
            assertDoesNotThrow(p::validateData);
        }

        @Test
        void nameAtBoundaryHundred() {
            var p = Product.builder().name("a".repeat(100)).version("v01.02.003").project(project()).build();
            assertEquals(100, p.getName().length());
            assertDoesNotThrow(p::validateData);
        }

        @Test
        void setDescriptionNull() {
            var p = Product.builder().name("Prod").version("v01.02.003").project(project()).build();
            p.setDescription("Something");
            p.setDescription(null);
            assertNull(p.getDescription());
        }

        @Test
        void setPublishDateNull() {
            var p = Product.builder().name("Prod").version("v01.02.003").project(project()).build();
            var date = LocalDateTime.now();
            p.setPublishDate(date);
            p.setPublishDate(null);
            assertNull(p.getPublishDate());
        }
    }
}
