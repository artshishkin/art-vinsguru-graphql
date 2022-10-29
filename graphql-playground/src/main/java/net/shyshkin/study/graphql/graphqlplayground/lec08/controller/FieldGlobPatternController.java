package net.shyshkin.study.graphql.graphqlplayground.lec08.controller;

import graphql.schema.DataFetchingFieldSelectionSet;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

@Slf4j
@Controller
public class FieldGlobPatternController {

    @QueryMapping
    public Object level1(DataFetchingFieldSelectionSet selectionSet) {
        log.debug("selectionSet: {}", selectionSet.getFields());
        log.debug("contains 'level2': {}", selectionSet.contains("level2"));    //true
        log.debug("contains 'level3': {}", selectionSet.contains("level3"));    //false
        log.debug("contains 'level2/level3': {}", selectionSet.contains("level2/level3"));          //true
        log.debug("contains '*/level3': {}", selectionSet.contains("level2/level3"));          //true
        log.debug("contains 'level2/level3/level4/level5': {}", selectionSet.contains("level2/level3/level4/level5"));  //true
        log.debug("contains 'level2/*/level4': {}", selectionSet.contains("level2/*/level4"));      //true
        log.debug("contains 'level2/**/level5': {}", selectionSet.contains("level2/**/level5"));    //true
        log.debug("contains '**/level5': {}", selectionSet.contains("**/level5"));  //true
        log.debug("contains '**/le?el5': {}", selectionSet.contains("**/le?el5"));  //true
        return null;
    }

}
