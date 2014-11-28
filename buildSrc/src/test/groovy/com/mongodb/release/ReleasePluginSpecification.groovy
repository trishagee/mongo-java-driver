package com.mongodb.release

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Specification

class ReleasePluginSpecification extends Specification {
    def 'should be able to add release plugin to a project and have it recognised'() {
        given:
        Project project = ProjectBuilder.builder().build()
        
        when:
        project.apply plugin: 'release'

        then:
        project.plugins.findPlugin(ReleasePlugin) != null
    }

//    def 'should add a task for drafting release notes to the project'() {
//        given:
//        Project project = ProjectBuilder.builder().build()
//
//        when:
//        project.apply plugin: 'release'
//
//        then:
//        project.tasks.draftReleaseNotes != null
//        project.tasks.draftReleaseNotes instanceof DraftReleaseNotesTask
//    }
//
}
