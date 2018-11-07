//  Copyright (c) Microsoft Corporation.
//  All rights reserved.
//
//  This code is licensed under the MIT License.
//
//  Permission is hereby granted, free of charge, to any person obtaining a copy
//  of this software and associated documentation files(the "Software"), to deal
//  in the Software without restriction, including without limitation the rights
//  to use, copy, modify, merge, publish, distribute, sublicense, and / or sell
//  copies of the Software, and to permit persons to whom the Software is
//  furnished to do so, subject to the following conditions :
//
//  The above copyright notice and this permission notice shall be included in
//  all copies or substantial portions of the Software.
//
//  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
//  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
//  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
//  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
//  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
//  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
//  THE SOFTWARE.
package com.microsoft.identity.client.internal.controllers;

import android.app.Activity;
import android.util.Pair;

import com.microsoft.identity.client.PublicClientApplicationConfiguration;
import com.microsoft.identity.client.UiBehavior;
import com.microsoft.identity.client.internal.authorities.Authority;
import com.microsoft.identity.client.internal.authorities.AzureActiveDirectoryAuthority;
import com.microsoft.identity.client.parameters.AcquireTokenParameters;
import com.microsoft.identity.common.internal.ui.AuthorizationAgent;
import com.microsoft.identity.common.internal.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class MSALAcquireTokenOperationParameters extends MSALOperationParameters {

    private Activity mActivity;
    private String mLoginHint;
    private UiBehavior mUIBehavior;
    private List<Pair<String, String>> mExtraQueryStringParameters;
    private List<String> mExtraScopesToConsent;

    public AuthorizationAgent getAuthorizationAgent() {
        return mAuthorizationAgent;
    }

    public void setAuthorizationAgent(AuthorizationAgent authorizationAgent) {
        mAuthorizationAgent = authorizationAgent;
    }

    private AuthorizationAgent mAuthorizationAgent;

    public Activity getActivity() {
        return mActivity;
    }

    public void setActivity(Activity mActivity) {
        this.mActivity = mActivity;
    }

    public UiBehavior getUIBehavior() {
        return mUIBehavior;
    }

    public void setUIBehavior(UiBehavior mUIBehavior) {
        this.mUIBehavior = mUIBehavior;
    }

    public List<Pair<String, String>> getExtraQueryStringParameters() {
        return mExtraQueryStringParameters;
    }

    public void setExtraQueryStringParameters(List<Pair<String, String>> mExtraQueryStringParameters) {
        this.mExtraQueryStringParameters = mExtraQueryStringParameters;
    }

    public List<String> getExtraScopesToConsent() {
        return mExtraScopesToConsent;
    }

    public void setExtraScopesToConsent(List<String> mExtraScopesToConsent) {
        this.mExtraScopesToConsent = mExtraScopesToConsent;
    }

    public void setLoginHint(String loginHint) {
        this.mLoginHint = loginHint;
    }

    public String getLoginHint() {
        return this.mLoginHint;
    }

    public static MSALAcquireTokenOperationParameters createMsalAcquireTokenOperationParameters(AcquireTokenParameters parameters, PublicClientApplicationConfiguration publicClientApplicationConfiguration){
        final String methodName = ":createMsalAcquireTokenOperationParameters";
        final MSALAcquireTokenOperationParameters params = new MSALAcquireTokenOperationParameters();

        if (StringUtil.isEmpty(parameters.getAuthority())) {
            params.setAuthority(publicClientApplicationConfiguration.getDefaultAuthority());
        } else {
            params.setAuthority(Authority.getAuthorityFromAuthorityUrl(parameters.getAuthority()));
        }

        if (params.getAuthority() instanceof AzureActiveDirectoryAuthority) {
            AzureActiveDirectoryAuthority aadAuthority = (AzureActiveDirectoryAuthority) params.getAuthority();
            aadAuthority.setMultipleCloudsSupported(publicClientApplicationConfiguration.getMultipleCloudsSupported());
        }

        com.microsoft.identity.common.internal.logging.Logger.verbosePII(
                methodName,
                "Using authority: [" + params.getAuthority().getAuthorityUri() + "]"
        );

        params.setScopes(new ArrayList<>(parameters.getScopes()));
        params.setClientId(publicClientApplicationConfiguration.getClientId());
        params.setRedirectUri(publicClientApplicationConfiguration.getRedirectUri());
        params.setActivity(parameters.getActivity());

        if(parameters.getAccount() != null){
            params.setLoginHint(parameters.getAccount().getUsername());
            params.setAccount(
                    getAccountInternal(
                            publicClientApplicationConfiguration.getClientId(),
                            null,
                            parameters.getAccount().getHomeAccountIdentifier().getIdentifier()
                    )
            );
        }else{
            params.setLoginHint(parameters.getLoginHint());
        }

        //TODO: Add token cache to publicClientconfiguration
        //params.setTokenCache();
        params.setExtraQueryStringParameters(parameters.getExtraQueryStringParameters());
        params.setExtraScopesToConsent(parameters.getExtraScopesToConsent());
        //TODO: Add app context to publicclientConfiguration
        //params.setAppContext(publicClientApplicationConfiguration.getApp);

        if (null != publicClientApplicationConfiguration.getAuthorizationAgent()) {
            params.setAuthorizationAgent(publicClientApplicationConfiguration.getAuthorizationAgent());
        } else {
            params.setAuthorizationAgent(AuthorizationAgent.DEFAULT);
        }

        if (parameters.getUIBehavior() == null) {
            params.setUIBehavior(UiBehavior.SELECT_ACCOUNT);
        } else {
            params.setUIBehavior(parameters.getUIBehavior());
        }

        return params;
    }
}
