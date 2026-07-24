<script lang="tsx">
/**
 * yudao DictTag 组件适配层。
 *
 * <p>根据字典类型 + 值，渲染对应颜色的 ElTag。
 * 对接 {@code @/utils/dict} 的 {@code getDictObj} 方法。</p>
 *
 * <p>用法：
 * {@code <dict-tag :type="DICT_TYPE.COMMON_STATUS" :value="row.status" />}</p>
 */
import { computed, defineComponent } from 'vue'
import type { PropType } from 'vue'
import { ElTag } from 'element-plus'
import { getDictObj } from '@/utils/dict'

/** ElTag type 属性的可选值 */
type ElTagType = 'primary' | 'success' | 'warning' | 'info' | 'danger'

/** 判断字符串是否为合法的十六进制颜色值 */
function isHexColor(color: string): boolean {
  return /^#([0-9A-Fa-f]{3}|[0-9A-Fa-f]{6})$/.test(color)
}

export default defineComponent({
  name: 'DictTag',
  props: {
    /** 字典类型（DICT_TYPE 枚举值） */
    type: {
      type: String as PropType<string>,
      required: true
    },
    /** 字典值（支持 number/string/boolean/数组） */
    value: {
      type: [String, Number, Boolean, Array] as PropType<
        string | number | boolean | Array<string | number | boolean>
      >,
      required: true
    },
    /** 字符串分隔符（仅当 value 为字符串时生效） */
    separator: {
      type: String as PropType<string>,
      default: ','
    },
    /** tag 之间的间隔 */
    gutter: {
      type: String as PropType<string>,
      default: '5px'
    }
  },
  setup(props) {
    /** 将 value 统一转换为字符串数组 */
    const valueArr = computed<string[]>(() => {
      if (typeof props.value === 'number' || typeof props.value === 'boolean') {
        return [String(props.value)]
      }
      if (typeof props.value === 'string') {
        return props.value.split(props.separator)
      }
      if (Array.isArray(props.value)) {
        return props.value.map(String)
      }
      return []
    })

    return () => {
      if (!props.type) return null
      if (
        props.value === undefined ||
        props.value === null ||
        props.value === ''
      ) {
        return null
      }
      const valueStrings = valueArr.value
      const tags = valueStrings
        .map((v) => {
          const dict = getDictObj(props.type, v)
          if (!dict) return null
          // primary / default 在 ElTag 中对应空字符串
          const colorType =
            dict.colorType === 'primary' || dict.colorType === 'default'
              ? ''
              : dict.colorType
          return (
            <ElTag
              style={dict.cssClass ? 'color: #fff' : ''}
              type={colorType ? (colorType as ElTagType) : undefined}
              color={
                dict.cssClass && isHexColor(dict.cssClass)
                  ? dict.cssClass
                  : ''
              }
              disableTransitions={true}
            >
              {dict.label}
            </ElTag>
          )
        })
        .filter(Boolean)
      return (
        <div
          class="dict-tag"
          style={{
            display: 'inline-flex',
            gap: props.gutter,
            justifyContent: 'center',
            alignItems: 'center'
          }}
        >
          {tags}
        </div>
      )
    }
  }
})
</script>
