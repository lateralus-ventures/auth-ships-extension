// Simple Lottie animation loader for Keycloak
// Falls back to CSS animation if Lottie is not available

// IMMEDIATE DEBUG - this should always show
console.log('🔥 LOTTIE SCRIPT EXECUTING - IMMEDIATE');
console.log('🔥 Current URL:', window.location.href);
console.log('🔥 Document ready state:', document.readyState);

(function() {
    'use strict';
    
    console.log('🔥 Inside IIFE wrapper');
    
    // Check if the lottie container exists
    const lottieContainer = document.querySelector('.kc-lottie-player');
    console.log('🔥 Lottie container search result:', lottieContainer);
    
    if (!lottieContainer) {
        console.log('🔥 NO LOTTIE CONTAINER FOUND - exiting early');
        // Let's see what containers DO exist
        const allDivs = document.querySelectorAll('div');
        console.log('🔥 All divs on page:', allDivs.length);
        console.log('🔥 Sample div classes:', Array.from(allDivs).slice(0, 5).map(d => d.className));
        return;
    }
    
    // Try to load the actual Lottie file - adjust path for Keycloak resources
    const lottieUrl = '/resources/j3qk5/login/askchief/img/Chief Demo v4 LOT.lottie';
    
    // Function to load Lottie via CDN if available
    function loadLottieAnimation() {
        console.log('🎭 Attempting to load Lottie animation...');
        console.log('📍 Lottie container found:', lottieContainer);
        console.log('🔗 Lottie URL:', lottieUrl);
        
        // First try to load lottie-web from CDN
        const script = document.createElement('script');
        script.src = 'https://cdnjs.cloudflare.com/ajax/libs/lottie-web/5.12.2/lottie.min.js';
        script.onload = function() {
            console.log('✅ Lottie CDN loaded successfully');
            if (typeof lottie !== 'undefined') {
                try {
                    console.log('🎬 Starting Lottie animation load...');
                    
                    // Clear the container
                    lottieContainer.innerHTML = '';
                    
                    // Load the Lottie animation
                    const animation = lottie.loadAnimation({
                        container: lottieContainer,
                        renderer: 'svg',
                        loop: true,
                        autoplay: true,
                        path: lottieUrl
                    });
                    
                    animation.addEventListener('complete', function() {
                        console.log('🎉 Lottie animation loaded successfully!');
                    });
                    
                    animation.addEventListener('error', function(e) {
                        console.log('❌ Lottie animation error:', e);
                        console.log('🔄 Keeping SVG fallback');
                    });
                    
                } catch (e) {
                    console.log('❌ Lottie loading failed:', e);
                    console.log('🔄 Keeping SVG fallback');
                }
            } else {
                console.log('❌ Lottie library not available after CDN load');
            }
        };
        script.onerror = function(e) {
            console.log('❌ Lottie CDN failed to load:', e);
            console.log('🔄 Keeping SVG fallback');
        };
        document.head.appendChild(script);
    }
    
    // Add some dynamic interaction to the SVG fallback
    function enhanceSVGAnimation() {
        const svg = lottieContainer.querySelector('svg');
        if (svg) {
            // Add click interaction
            svg.addEventListener('click', function() {
                svg.style.transform = 'scale(0.95)';
                setTimeout(() => {
                    svg.style.transform = 'scale(1)';
                }, 150);
            });
            
            // Add random subtle movements
            setInterval(() => {
                if (Math.random() > 0.7) {
                    svg.style.transform = 'translateY(-2px) scale(1.01)';
                    setTimeout(() => {
                        svg.style.transform = 'translateY(0) scale(1)';
                    }, 300);
                }
            }, 3000);
        }
    }
    
    // Initialize when DOM is ready
    console.log('🚀 Lottie loader script started');
    console.log('📊 Document ready state:', document.readyState);
    
    if (document.readyState === 'loading') {
        console.log('⏳ Waiting for DOM to load...');
        document.addEventListener('DOMContentLoaded', function() {
            console.log('✅ DOM loaded, initializing...');
            enhanceSVGAnimation();
            // Try to load Lottie after a delay
            setTimeout(loadLottieAnimation, 500);
        });
    } else {
        console.log('✅ DOM already loaded, initializing immediately...');
        enhanceSVGAnimation();
        setTimeout(loadLottieAnimation, 500);
    }
})();