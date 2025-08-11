<#import "template.ftl" as layout>

<!-- Ask Chief Lottie Animation - positioned above everything -->
<div class="kc-lottie-container-header">
    <div class="kc-lottie-container">
        <div class="kc-lottie-player">
            <img src="${url.resourcesPath}/img/askchief-logo.svg" alt="Ask Chief" style="width: 150px; height: 150px;" />
        </div>
    </div>
</div>

<@layout.registrationLayout displayMessage=!messagesPerField.existsError('username','password') displayInfo=realm.password && realm.registrationAllowed && !registrationDisabled??; section>
    <#if section = "header">
        <div class="kc-custom-header">
            <h1>Ask Chief</h1>
            <p>AI-Powered Shipping Intelligence Platform</p>
        </div>
    <#elseif section = "form">
    <div class="kc-welcome-back">
        <h2>Welcome back</h2>
    </div>
    <div id="kc-form">
      <div id="kc-form-wrapper">
        <#if realm.password>
            <form id="kc-form-login" onsubmit="login.disabled = true; return true;" action="${url.loginAction}" method="post">
                <#if !usernameHidden??>
                    <div class="${properties.kcFormGroupClass!}">
                        <input tabindex="1" id="username" class="${properties.kcInputClass!}" name="username"
                               value="${(login.username!'')}"
                               type="text" autofocus autocomplete="off"
                               placeholder="Email address"
                               aria-invalid="<#if messagesPerField.existsError('username','password')>true</#if>"
                        />

                        <#if messagesPerField.existsError('username','password')>
                            <span id="input-error" class="${properties.kcInputErrorMessageClass!}" aria-live="polite">
                                        ${kcSanitize(messagesPerField.getFirstError('username','password'))?no_esc}
                            </span>
                        </#if>

                    </div>
                </#if>

                <div class="${properties.kcFormGroupClass!}">
                    <input tabindex="2" id="password" class="${properties.kcInputClass!}" name="password"
                           type="password" autocomplete="off"
                           placeholder="Password"
                           aria-invalid="<#if messagesPerField.existsError('username','password')>true</#if>"
                    />

                    <#if usernameHidden?? && messagesPerField.existsError('username','password')>
                        <span id="input-error" class="${properties.kcInputErrorMessageClass!}" aria-live="polite">
                                    ${kcSanitize(messagesPerField.getFirstError('username','password'))?no_esc}
                        </span>
                    </#if>

                </div>

                <#if realm.rememberMe && !usernameHidden??>
                    <div class="kc-remember-me-row">
                        <div class="checkbox">
                            <input tabindex="3" id="rememberMe" name="rememberMe" type="checkbox"
                                   <#if login.rememberMe??>checked</#if>>
                            <label for="rememberMe">Remember me</label>
                        </div>
                    </div>
                </#if>

                  <div id="kc-form-buttons" class="${properties.kcFormGroupClass!}">
                      <input type="hidden" id="id-hidden-input" name="credentialId" <#if auth.selectedCredential?has_content>value="${auth.selectedCredential}"</#if>/>
                      <input tabindex="4" class="${properties.kcButtonClass!} ${properties.kcButtonPrimaryClass!} ${properties.kcButtonBlockClass!} ${properties.kcButtonLargeClass!}" name="login" id="kc-login" type="submit" value="Continue"/>
                  </div>
                
                <#if realm.resetPasswordAllowed>
                    <div class="kc-forgot-password">
                        <a tabindex="5" href="${url.loginResetCredentialsUrl}">Forgot Password?</a>
                    </div>
                </#if>
            </form>
        </#if>
        </div>

        <!-- Social login section removed completely -->

    </div>
    <#elseif section = "info" >
        <#if realm.password && realm.registrationAllowed && !registrationDisabled??>
            <div id="kc-registration-container">
                <div id="kc-registration">
                    <span>${msg("noAccount")} <a tabindex="6"
                                                 href="${url.registrationUrl}">${msg("doRegister")}</a></span>
                </div>
            </div>
        </#if>
    </#if>

</@layout.registrationLayout>