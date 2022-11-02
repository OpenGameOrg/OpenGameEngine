$input a_position, a_normal, a_texcoord0
$output v_normal, v_texcoord0

#include "../common/common.sh"

void main() {
    gl_Position = mul(u_modelViewProj, vec4(a_position, 1));
    v_normal = mul(u_model[0], vec4(a_normal, 0.0)).xyz;
    v_texcoord0 = a_texcoord0;
}