package com.dp.plat.project.event;

import org.springframework.context.ApplicationEvent;

/**
 * Event published after a final acceptance application is approved.
 *
 * <p>Listeners (e.g. the asset module) can react to this event to perform
 * cross-domain actions such as recycling project-bound equipment.</p>
 */
public class FinalAcceptanceApprovedEvent extends ApplicationEvent {

    private final Long projectId;

    /**
     * Create a new {@code FinalAcceptanceApprovedEvent}.
     *
     * @param source    the object on which the event initially occurred
     * @param projectId the id of the project whose final acceptance was approved
     */
    public FinalAcceptanceApprovedEvent(Object source, Long projectId) {
        super(source);
        this.projectId = projectId;
    }

    /**
     * Get the project id whose final acceptance was approved.
     *
     * @return project id
     */
    public Long getProjectId() {
        return projectId;
    }
}
