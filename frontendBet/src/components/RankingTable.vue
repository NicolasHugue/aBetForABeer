<script>
export default {
  props: {
    headers: { type: Array, required: true },
    items: { type: Array, require: true },
    fields: { type: Array, require: true },
  },
};
</script>

<template>
  <div class="overflow-x-auto -mx-4 sm:mx-0">
    <table
      class="min-w-[780px] w-full border-collapse bg-white rounded-lg shadow-lg overflow-hidden"
    >
      <thead class="bg-gray-800 text-white px-4 py-2 uppercase">
        <tr>
          <th
            v-for="h in headers"
            :key="h"
            class="px-4 py-2 text-left font-semibold whitespace-nowrap"
          >
            {{ h }}
          </th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="item in items" :key="item.id ?? item.name" class="border-t hover:bg-gray-100">
          <td v-for="f in fields" :key="f" class="px-4 py-2 whitespace-nowrap">
            <slot :name="'row-' + f" :item="item">
              {{ item[f] }}
            </slot>
          </td>
        </tr>
        <tr v-if="items.length === 0">
          <td :colspan="headers.length" class="px-4 py-2 text-center text-gray-500">
            Aucune donnée
          </td>
        </tr>
      </tbody>
    </table>
  </div>
</template>
