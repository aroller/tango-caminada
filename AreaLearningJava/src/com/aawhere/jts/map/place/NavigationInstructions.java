package com.aawhere.jts.map.place;

import com.aawhere.lang.ObjectBuilder;

/**
 * Created by aroller on 7/6/15.
 */
public class NavigationInstructions {

    /**
     * @see #instruction()
     */
    private PlaceNavigator.Instruction instruction;
    /**
     * @see #distance()
     */
    private Double distance;

    /**
     * @see #bearing()
     */
    private Double bearing;

    /**
     * Used to construct all instances of NavigationInstructions.
     */
    public static class NavigationInstructionsBuilder extends ObjectBuilder<NavigationInstructions> {

        private NavigationInstructionsBuilder() {
            super(new NavigationInstructions());
        }


        /**
         * @see #bearing()
         */
        public NavigationInstructionsBuilder bearing(Double bearing) {
            building.bearing = bearing;
            return this;
        }


        /**
         * @see #distance()
         */
        public NavigationInstructionsBuilder distance(Double distance) {
            building.distance = distance;
            return this;
        }


        /**
         * @see #instruction()
         */
        public NavigationInstructionsBuilder instruction(PlaceNavigator.Instruction instruction) {
            building.instruction = instruction;
            return this;
        }


        public NavigationInstructions build() {
            NavigationInstructions built = super.build();
            return built;
        }

    }//end Builder

    public static NavigationInstructionsBuilder builder() {
        return new NavigationInstructionsBuilder();
    }

    /**
     * Use {@link NavigationInstructionsBuilder} to construct NavigationInstructions
     */
    private NavigationInstructions() {
    }

    /**
     * @return the relative angle (in radians) from the current heading to adjust in order to reach a destination
     */
    public Double bearing() {
        return this.bearing;
    }

    /**
     * @return the distance, in map coordinate units, to the target
     */
    public Double distance() {
        return this.distance;
    }

    /**
     * @return the command to be executed to reach a destination
     */
    public PlaceNavigator.Instruction instruction() {
        return this.instruction;
    }
}
