/*
 * Copyright 2014 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *  http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
package com.amazonaws.resources.internal;

import java.util.List;
import java.util.NoSuchElementException;

import com.amazonaws.AmazonWebServiceRequest;
import com.amazonaws.resources.ResultCapture;
import com.amazonaws.resources.internal.model.ActionModel;


/**
 * A single page of resources from a collection. In addition to the
 * list of resources on this page, it tracks a 'next token' which, if not
 * {@code null}, can be redeemed for the next page of resources
 * in the collection.
 */
public class ResourcePageImpl {

    private final ActionContext context;
    private final ActionModel listActionModel;
    private final AmazonWebServiceRequest request;
    private final ActionResult result;

    public ResourcePageImpl(
            ActionContext context,
            ActionModel listActionModel,
            AmazonWebServiceRequest request,
            ActionResult result) {

        this.context = context;
        this.listActionModel = listActionModel;
        this.request = request;
        this.result = result;
    }

    /**
     * @return the list of resources which comprise this page
     */
    public List<ResourceImpl> getResources() {
        return result.getResources();
    }

    /**
     * @return the extracted next page token (or null if no more pages)
     */
    public Object getNextToken() {
        return result.getToken();
    }

    /**
     * @return true if there is a subsequent page of resources to be retrieved
     */
    public boolean hasNextPage() {
        return (getNextToken() != null);
    }

    /**
     * Makes a request to the service to retrieve the next page of resources
     * in the collection.
     *
     * @param extractor an optional result extractor object
     * @return the next page of results
     */
    public ResourcePageImpl nextPage(ResultCapture<Object> extractor) {
        if (getNextToken() == null) {
            throw new NoSuchElementException("There is no next page");
        }

        ActionResult result = ActionUtils.perform(
                context,
                listActionModel,
                request,
                extractor,
                getNextToken());

        return new ResourcePageImpl(
                context,
                listActionModel,
                request,
                result);
    }
}
