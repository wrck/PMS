package com.dp.plat.core.tags;

import org.sitemesh.SiteMeshContext;
import org.sitemesh.content.ContentProperty;
import org.sitemesh.content.tagrules.TagRuleBundle;
import org.sitemesh.content.tagrules.html.ExportTagToContentRule;
import org.sitemesh.tagprocessor.State;

public class SiteMeshExtTagRuleBundle implements TagRuleBundle {

	@Override
	public void cleanUp(State defaultState, ContentProperty contentProperty, SiteMeshContext siteMeshContext) {
	}

	@Override
	public void install(State defaultState, ContentProperty contentProperty, SiteMeshContext siteMeshContext) {
		defaultState.addRule("cssTag", new ExportTagToContentRule(siteMeshContext, contentProperty.getChild("cssTag"), false));
		defaultState.addRule("jsTag", new ExportTagToContentRule(siteMeshContext, contentProperty.getChild("jsTag"), false));
	}

}
