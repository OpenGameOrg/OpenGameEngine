$input a_position, a_normal
$output v_normal, v_position

#include "../common/common.sh"

void main() {
    v_position = mul(u_model[0], vec4(a_position, 1.0)).xyz;
    v_normal = normalize(mul(u_model[0], vec4(a_normal, 0.0) ).xyz);

    gl_Position = mul(u_modelViewProj, vec4(a_position, 1));
}