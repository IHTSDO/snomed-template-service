package org.ihtsdo.otf.transformationandtemplate.service.script

import spock.lang.Specification

class Update_Lat_RefsetTest extends Specification {
    def "test extractShortName valid input : #input"() {
        given: "A branchPath"
            Update_Lat_Refset updateLatRefset = new Update_Lat_Refset(null, null)

        expect:
            output == updateLatRefset.extractShortName(input)

        where:
            input                             || output
            "MAIN"                            || "SNOMEDCT"
            "MAIN/projectA"                   || "SNOMEDCT"
            "MAIN/projectA/"                  || "SNOMEDCT"
            "MAIN/projectA/taskA"             || "SNOMEDCT"
            "MAIN/SNOMEDCT-XX"                || "SNOMEDCT-XX"
            "MAIN/SNOMEDCT-XX/projectA"       || "SNOMEDCT-XX"
            "MAIN/SNOMEDCT-XX/projectA/taskA" || "SNOMEDCT-XX"
    }

    def "test extractShortName invalid input : #input"() {
        given: "A branchPath"
            Update_Lat_Refset updateLatRefset = new Update_Lat_Refset(null, null)

        when:
            updateLatRefset.extractShortName(input)

        then:
            def ex = thrown(expectedException)
            ex.message == message

        where:
            input                                || expectedException        | message
            null                                 || IllegalArgumentException | "Branch path must contain at least MAIN"
            ""                                   || IllegalArgumentException | "Branch path must contain at least MAIN"
            "NOTMAIN/projectA"                   || IllegalArgumentException | "Branch path must contain a short name and start with MAIN"
            "NOTMAIN/projectA/"                  || IllegalArgumentException | "Branch path must contain a short name and start with MAIN"
            "NOTMAIN/projectA/taskA"             || IllegalArgumentException | "Branch path must contain a short name and start with MAIN"
            "NOTMAIN/SNOMEDCT-XX"                || IllegalArgumentException | "Branch path must contain a short name and start with MAIN"
            "NOTMAIN/SNOMEDCT-XX/projectA"       || IllegalArgumentException | "Branch path must contain a short name and start with MAIN"
            "NOTMAIN/SNOMEDCT-XX/projectA/taskA" || IllegalArgumentException | "Branch path must contain a short name and start with MAIN"
    }
}
