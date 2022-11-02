$input v_normal, v_texcoord0

#include "../common/common.sh"

SAMPLER2D(s_texColor,  0);

void main()
{
    vec3 lightDir = vec3(0.0, 0.0, -1.0);
    float ndotl = dot(normalize(v_normal), lightDir);
    float spec = pow(ndotl, 30.0);

    vec4 color = toLinear(texture2D(s_texColor, v_texcoord0) );
    gl_FragColor = vec4(pow(pow(color.xyz, vec3_splat(2.2)) * ndotl + spec, vec3_splat(1.0 / 2.2)), 1.0);
}