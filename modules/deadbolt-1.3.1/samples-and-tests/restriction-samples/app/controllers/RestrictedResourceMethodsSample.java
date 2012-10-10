/*
 * Copyright 2010-2011 Steve Chaloner
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package controllers;

import controllers.deadbolt.Deadbolt;
import controllers.deadbolt.Restrict;
import controllers.deadbolt.RestrictedResource;
import play.mvc.Controller;
import play.mvc.With;

/**
 * @author Steve Chaloner (steve@objectify.be).
 */
@With(Deadbolt.class)
public class RestrictedResourceMethodsSample extends Controller
{
    public static void index()
    {
        render();
    }

    @RestrictedResource(name = "resourceA")
    public static void allowed()
    {
        render("authorised.html");
    }

    @RestrictedResource(name = "resourceB")
    public static void notSpecified()
    {
        render("unauthorised.html");
    }

    @RestrictedResource(name = "resourceB", staticFallback = true)
    public static void notSpecifiedWithStaticFallbackAndNoStaticRestrictions()
    {
        render("authorised.html");
    }

    @RestrictedResource(name = "resourceB", staticFallback = true)
    @Restrict("oof")
    public static void notSpecifiedWithStaticFallbackAndBlockingStaticRestriction()
    {
        render("unauthorised.html");
    }

    @RestrictedResource(name = "resourceB", staticFallback = true)
    @Restrict("foo")
    public static void notSpecifiedWithStaticFallbackAndOkStaticRestriction()
    {
        render("authorised.html");
    }

    @RestrictedResource(name = "resourceC")
    public static void denied()
    {
        render("unauthorised.html");
    }
}
