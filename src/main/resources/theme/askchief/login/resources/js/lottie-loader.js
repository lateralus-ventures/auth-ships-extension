// Simple Lottie animation loader for Keycloak
// Falls back to CSS animation if Lottie is not available

(function() {
    'use strict';
    
    // Check if the lottie container exists
    const lottieContainer = document.querySelector('.kc-lottie-player');
    if (!lottieContainer) {
        return;
    }
    
    // Try to load the actual Lottie file if possible
    const lottieUrl = '/realms/master/askchief/login/resources/img/Chief Demo v4 LOT.lottie';
    
    // Function to load Lottie via CDN if available
    function loadLottieAnimation() {
        // First try to load lottie-web from CDN
        const script = document.createElement('script');
        script.src = 'https://cdnjs.cloudflare.com/ajax/libs/lottie-web/5.12.2/lottie.min.js';
        script.onload = function() {
            if (typeof lottie !== 'undefined') {
                try {
                    // Clear the container
                    lottieContainer.innerHTML = '';
                    
                    // Load the Lottie animation
                    lottie.loadAnimation({
                        container: lottieContainer,
                        renderer: 'svg',
                        loop: true,
                        autoplay: true,
                        path: lottieUrl
                    });
                } catch (e) {
                    console.log('Lottie loading failed, keeping SVG fallback');
                }
            }
        };
        script.onerror = function() {
            console.log('Lottie CDN failed, keeping SVG fallback');
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
    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', function() {
            enhanceSVGAnimation();
            // Try to load Lottie after a delay
            setTimeout(loadLottieAnimation, 500);
        });
    } else {
        enhanceSVGAnimation();
        setTimeout(loadLottieAnimation, 500);
    }
})();